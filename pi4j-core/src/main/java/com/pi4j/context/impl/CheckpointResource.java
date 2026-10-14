package com.pi4j.context.impl;

import com.pi4j.exception.LifecycleException;
import com.pi4j.exception.RestoreException;
import com.pi4j.io.Checkpointable;
import com.pi4j.io.IO;
import org.crac.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

/**
 * CRaC (Coordinated Restore at Checkpoint) {@link Resource} that lets a Pi4J context survive a checkpoint.
 * <p>
 * Before a checkpoint, every registered {@link Checkpointable} I/O instance is suspended (in reverse creation
 * order) so no native file descriptors remain open. After a restore, the board information is re-detected and
 * the instances are resumed in creation order, so application code can keep using the same objects.
 * <p>
 * {@link DefaultContext} registers an instance with the global CRaC context and holds the strong reference
 * that the CRaC API requires. On a JVM without CRaC support the registration is a no-op.
 *
 * @see <a href="https://docs.azul.com/crac/usage/integrating-crac">Integrating CRaC</a>
 */
public class CheckpointResource implements Resource {

    private static final Logger logger = LoggerFactory.getLogger(CheckpointResource.class);

    private final DefaultContext context;
    private final MutableRegistry registry;

    // Suspended instances; the most recently pushed (earliest created) is resumed first.
    private final Deque<IO> suspended = new ArrayDeque<>();

    CheckpointResource(DefaultContext context, MutableRegistry registry) {
        this.context = context;
        this.registry = registry;
    }

    /**
     * Suspends all registered {@link Checkpointable} I/O instances. If one of them fails, the instances
     * suspended so far are resumed again, so the context is left as it was before the attempt.
     *
     * @param craContext the CRaC context invoking this resource (unused)
     * @throws LifecycleException if an I/O instance cannot be suspended
     */
    @Override
    public synchronized void beforeCheckpoint(org.crac.Context<? extends Resource> craContext) throws LifecycleException {
        if (context.isShutdown()) {
            return;
        }
        logger.info("Suspending Pi4J I/O instances before checkpoint...");
        var instances = new ArrayList<IO>(registry.all().values());
        for (var io : instances.reversed()) {
            if (!(io instanceof Checkpointable checkpointable)) {
                continue;
            }
            try {
                checkpointable.suspend();
                suspended.push(io);
            } catch (RuntimeException e) {
                var failure = new LifecycleException("Failed to suspend IO '" + io.id() + "' before checkpoint", e);
                try {
                    resumeAll();
                } catch (RestoreException restoreFailure) {
                    failure.addSuppressed(restoreFailure);
                }
                throw failure;
            }
        }
        logger.info("Suspended {} Pi4J I/O instance(s).", suspended.size());
    }

    /**
     * Re-detects the board information and resumes all I/O instances suspended by
     * {@link #beforeCheckpoint(org.crac.Context)}. Every instance is attempted, even if an earlier one fails.
     *
     * @param craContext the CRaC context invoking this resource (unused)
     * @throws RestoreException if one or more I/O instances cannot be resumed; each failure is attached as a
     *                          suppressed exception
     */
    @Override
    public synchronized void afterRestore(org.crac.Context<? extends Resource> craContext) throws RestoreException {
        if (context.isShutdown()) {
            suspended.clear();
            return;
        }
        logger.info("Restoring Pi4J context after checkpoint...");
        context.redetectBoardInfo();
        resumeAll();
        logger.info("Pi4J context restored.");
    }

    private void resumeAll() throws RestoreException {
        var failures = new ArrayList<RestoreException>();
        while (!suspended.isEmpty()) {
            var io = suspended.pop();
            try {
                ((Checkpointable) io).resume();
            } catch (RuntimeException e) {
                logger.error("Failed to restore IO '{}': {}", io.id(), e.getMessage());
                failures.add(new RestoreException("Failed to restore IO '" + io.id() + "': " + e.getMessage(), e));
            }
        }
        if (failures.size() == 1) {
            throw failures.getFirst();
        }
        if (!failures.isEmpty()) {
            var failure = new RestoreException("Failed to restore " + failures.size() + " Pi4J I/O instances");
            failures.forEach(failure::addSuppressed);
            throw failure;
        }
    }
}
