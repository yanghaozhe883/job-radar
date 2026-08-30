import Foundation
import Combine

// ============================================================
// Generic MVI ViewModel base — mirrors Android's `core/mvi/MviViewModel`.
// The UI only calls `onEvent`; state changes flow out via `$state`.
// ============================================================

@MainActor
class MviViewModel<Contract: MviContract>: ObservableObject {
    typealias State = Contract.State
    typealias Event = Contract.Event
    typealias Effect = Contract.Effect

    @Published private(set) var state: State
    private let effectSubject = PassthroughSubject<Effect, Never>()

    var effects: AnyPublisher<Effect, Never> { effectSubject.eraseToAnyPublisher() }

    init(initialState: State) {
        self.state = initialState
    }

    /// The single entry point for every user intent.
    func onEvent(_ event: Event) {
        Task { await handle(event) }
    }

    /// Reducer: turn one event into a new State or an Effect.
    @MainActor func handle(_ event: Event) async {
        fatalError("Subclasses must implement handle(_:)")
    }

    /// Replace state (immutable transition).
    @MainActor func reduce(_ next: State) {
        state = next
    }

    /// Emit a one-shot side effect (navigation, toast, haptic...).
    @MainActor func emit(_ effect: Effect) {
        effectSubject.send(effect)
    }
}
