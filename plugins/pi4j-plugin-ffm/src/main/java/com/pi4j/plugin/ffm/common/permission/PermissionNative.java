package com.pi4j.plugin.ffm.common.permission;

import com.pi4j.exception.Pi4JException;
import com.pi4j.plugin.ffm.common.permission.structs.GroupData;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Arrays;

import static com.pi4j.plugin.ffm.common.Pi4JNativeContext.CAPTURED_STATE_LAYOUT;
import static com.pi4j.plugin.ffm.common.Pi4JNativeContext.processError;

/**
 * Java front end to the glibc group-database calls bound by {@link PermissionContext}, used to find out
 * which groups a user belongs to (so Pi4J can verify membership of e.g. {@code gpio}/{@code i2c}/{@code spi}
 * before opening device nodes). Each method follows the same pattern:
 * <ul>
 *   <li>allocate the needed buffers from a per-call {@link Arena#ofConfined()} arena;</li>
 *   <li>attach an {@code errno} capture state so failures can be reported;</li>
 *   <li>invoke the native libc handle;</li>
 *   <li>translate failures into a {@link Pi4JException} via {@code processError};</li>
 *   <li>decode and return any result.</li>
 * </ul>
 */
public class PermissionNative {
    // Keep the context field to trigger PermissionContext class loading (and thus MethodHandle init).
    @SuppressWarnings("unused")
    private final PermissionContext context = new PermissionContext();


    /**
     * Rewinds the system group database to its first entry by calling {@code setgrent}, so that a
     * subsequent sequence of {@link #getNextGroup()} calls iterates the database from the start.
     *
     * @throws Pi4JException if the native call cannot be invoked
     */
    public void openGroupDatabase() {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            PermissionContext.SET_GR_ENT.invoke(capturedState);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Returns the next entry from the system group database by calling {@code getgrent}, dereferencing
     * the returned {@code struct group *} into a {@link GroupData}. Call {@link #openGroupDatabase()}
     * first and {@link #closeGroupDatabase()} when finished.
     *
     * @return the next group's data, or {@code null} once the end of the database is reached
     * @throws Pi4JException if the native call fails or the result cannot be decoded
     */
    public GroupData getNextGroup() {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var callResult = (MemorySegment) PermissionContext.GET_GR_ENT.invoke(capturedState);
            if (callResult.equals(MemorySegment.NULL)) {
                return null;
            }
            var groupMemorySegment = callResult.reinterpret(8).get(ValueLayout.ADDRESS, 0);
            return GroupData.create(groupMemorySegment.reinterpret(GroupData.LAYOUT.byteSize()));
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }


    /**
     * Closes the system group database and releases its resources by calling {@code endgrent}, ending
     * an iteration begun with {@link #openGroupDatabase()}.
     *
     * @throws Pi4JException if the native call cannot be invoked
     */
    public void closeGroupDatabase() {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            PermissionContext.END_GR_ENT.invoke(capturedState);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Returns the supplementary group ids that the given user belongs to by calling {@code getgrouplist}.
     * The user's primary group is passed as {@code -1} and stripped from the result, so only the
     * supplementary groups remain.
     *
     * @param username the login name to look up group membership for
     * @return the user's supplementary group ids, excluding the primary group
     * @throws Pi4JException if {@code getgrouplist} reports an error or the call cannot be invoked
     */
    public int[] getGroupList(String username) {
        try (var arena = Arena.ofConfined()) {
            var usernameMemorySegment = arena.allocateFrom(username);
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            // make it as much as possible (1024 groups)
            var groupsMemorySegment = arena.allocateFrom(ValueLayout.JAVA_INT, new int[1024]);
            // this value will contain the real group count when returned
            var ngroups = arena.allocateFrom(ValueLayout.JAVA_INT, 1024);
            // call with user group of -1 and exclude it from the list
            var callResult = (int) PermissionContext.GET_GROUP_LIST.invoke(capturedState, usernameMemorySegment,
                -1, groupsMemorySegment, ngroups);
            processError(callResult, capturedState, "getGroupList", username);
            var groupsCount = ngroups.get(ValueLayout.JAVA_INT, 0);
            // make effective group list in array (exclude the first one)
            return Arrays.copyOfRange(groupsMemorySegment.toArray(ValueLayout.JAVA_INT), 1, groupsCount);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Looks up a single group by its numeric id via {@code getgrgid} and decodes the returned
     * {@code struct group *} into a {@link GroupData} (carrying the group name).
     *
     * @param groupId the numeric group id (GID) to resolve
     * @return the group's metadata; an empty {@link GroupData} if no matching group exists
     * @throws Pi4JException if the lookup fails or the result cannot be decoded
     */
    public GroupData getGroupData(int groupId) {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var callResult = (MemorySegment) PermissionContext.GET_GROUP_DATA.invoke(capturedState, groupId);
            processError(callResult.equals(MemorySegment.NULL) ? -1 : 0, capturedState, "getGroupData", groupId);
            // since the return value of function is pointer, we need to reinterpret it first to 8 bytes (address size in Java),
            // and then get content address itself.
            var groupMemorySegment = callResult.reinterpret(8).get(ValueLayout.ADDRESS, 0);
            // create a group data object from dereferenced result
            return GroupData.create(groupMemorySegment.reinterpret(GroupData.LAYOUT.byteSize()));
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }
}
