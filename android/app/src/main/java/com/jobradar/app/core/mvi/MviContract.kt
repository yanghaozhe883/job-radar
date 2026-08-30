package com.jobradar.app.core.mvi

/** Immutable UI state snapshot. */
interface MviState

/** One user intent. */
interface MviEvent

/** One-shot side effect (navigate, toast, haptic...). */
interface MviEffect

/**
 * The single contract every feature follows to guarantee a strict
 * unidirectional data flow (UDF) and clean UI<->business decoupling.
 *
 * Architecture contract (MVI):
 *  - [State]  — immutable description of what the UI renders right now.
 *  - [Event]  — user intent, the ONLY input the UI is allowed to send.
 *  - [Effect] — one-shot side effects (toast, navigation, haptic) that the UI
 *               observes but does not store in [State].
 *
 * Rules enforced by design:
 *  1. The UI never mutates state directly; it only pushes [Event]s.
 *  2. The ViewModel is the single source of truth; it reduces events into new
 *     immutable [State] copies and emits [Effect]s for side effects.
 *  3. Business logic never lives in the View layer — it is delegated to the
 *     domain use cases.
 */
interface MviContract<State : MviState, Event : MviEvent, Effect : MviEffect>
