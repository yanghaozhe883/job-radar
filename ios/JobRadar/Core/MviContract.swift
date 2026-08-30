import Foundation

// ============================================================
// MVI contract — mirrors Android's `core/mvi/MviContract`.
// Strict unidirectional data flow: UI -> Event -> reducer -> State (+ Effect).
// ============================================================

protocol MviState {}
protocol MviEvent {}
protocol MviEffect {}

/// The contract each feature implements: State/Event/Effect + a pure reducer.
protocol MviContract {
    associatedtype State: MviState
    associatedtype Event: MviEvent
    associatedtype Effect: MviEffect
}
