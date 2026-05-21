package com.ling.lingaicodegeneration.ai.multiagent.model;

public record CodeGenInput(
        RequirementSpec requirementSpec,
        String imageListStr,
        TaskGraph taskGraph
) {
    public String buildPrompt() {
        StringBuilder sb = new StringBuilder(requirementSpec.getProjectDescription());

        if (imageListStr != null && !imageListStr.isBlank()) {
            sb.append("\n\n## Available Image Resources\n");
            sb.append("Please use the following images in the generated website, ")
              .append("place them in appropriate positions:\n");
            sb.append(imageListStr);
        }

        String hints = taskGraph.codeGenHints();
        if (hints != null && !hints.isBlank()) {
            sb.append("\n\n## Additional Instructions\n");
            sb.append(hints);
        }

        return sb.toString();
    }
}
