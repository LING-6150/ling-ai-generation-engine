package com.ling.lingaicodegeneration.ai.multiagent.agent;

/**
 * Core agent contract for the multi-agent pipeline.
 *
 * <p>When {@code O} is {@code Flux<String>}, the emitted items are <em>bare tokens</em>
 * (raw LLM partial-response strings), NOT pre-formatted SSE events.  Formatting
 * into SSE-ready JSON (e.g. {@code {"type":"code_token","detail":"..."}} ) is the
 * responsibility of the <strong>OrchestratorAgent</strong>, which wraps each token
 * via {@code sink.next(event("code_token", token))} before forwarding downstream.
 *
 * <p>Error handling contract for streaming agents:
 * <ul>
 *   <li>Non-orchestrator agents (CodeGenAgent, RefineAgent) MAY call
 *       {@code sink.error()} — errors propagate to OrchestratorAgent's
 *       {@code blockLast()} and are caught by its local try-catch.</li>
 *   <li>OrchestratorAgent MUST NOT call {@code sink.error()} on its outer
 *       Flux sink.  Spring MVC (spring-boot-starter-web) has no
 *       HttpMessageConverter for {@code LinkedHashMap → text/event-stream};
 *       doing so causes {@code HttpMessageNotWritableException}.  Instead,
 *       emit a {@code workflow_error} event and call {@code sink.complete()}.</li>
 * </ul>
 *
 * @param <I> input type
 * @param <O> output type
 */
@FunctionalInterface
public interface Agent<I, O> {
    O execute(I input, AgentContext context);
}
