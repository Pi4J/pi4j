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
 * Class for calling native permission methods.
 * The logic behind the class is follows:
 * - allocate the needed buffers from a per-call {@link Arena#ofConfined()} arena
 * - optionally add 'errno' context to caller
 * - call native function with 'invoke'
 * - process errors if any captured by 'errno'
 * - return call result if needed
 */
public class PermissionNative {
    // Keep the context field to trigger PermissionContext class loading (and thus MethodHandle init).
    @SuppressWarnings("unused")
    private final PermissionContext context = new PermissionContext();


    public void openGroupDatabase() {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            PermissionContext.SET_GR_ENT.invoke(capturedState);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

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


    public void closeGroupDatabase() {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            PermissionContext.END_GR_ENT.invoke(capturedState);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Gets an array of group ids, where current user corresponds.
     *
     * @param username username the check against
     * @return array of group ids
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
     * Gets the group metadata (e.g. group name) by given group id.
     *
     * @param groupId id of given group
     * @return group meta data
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
