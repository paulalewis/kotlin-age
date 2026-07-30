package com.castlefrog.agl

/**
 * Marker interface for domain actions.
 *
 * Implementing this interface indicates that a type is an **action** in a given
 * game domain (for example, dropping a piece in Connect 4 or placing a stone in
 * Hex). It exists for type safety and domain tagging only; it defines no
 * methods. Shared behavior should be added deliberately later if needed, not
 * assumed to exist here.
 *
 * ## Conventions for implementations
 * - **Immutability** — actions must be immutable once constructed. They are
 *   shared freely across legal-action sets, caches, agents, and history, and
 *   must not change after creation.
 * - **Value equality** — prefer value-based [Any.equals] / [Any.hashCode]
 *   (e.g. data classes or equivalent) so the same move compares equal across
 *   instances.
 * - **Thread-safety** — immutability makes actions safe to share across threads
 *   without synchronization.
 */
interface Action
