package spring.project.finance_manager.component;

import java.util.List;

public class GeminiResponse {
    private List<Candidate> candidates;
    private UsageMetadata usageMetadata;
    private String modelVersion;

    public List<Candidate> getCandidates() {
        return candidates;
    }
    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }
    public UsageMetadata getUsageMetadata() {
        return usageMetadata;
    }
    public void setUsageMetadata(UsageMetadata usageMetadata) {
        this.usageMetadata = usageMetadata;
    }
    public String getModelVersion() {
        return modelVersion;
    }
    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public static class Candidate {
        private Content content;
        private String finishReason;
        private double avgLogprobs;

        public Content getContent() {
            return content;
        }
        public void setContent(Content content) {
            this.content = content;
        }
        public String getFinishReason() {
            return finishReason;
        }
        public void setFinishReason(String finishReason) {
            this.finishReason = finishReason;
        }
        public double getAvgLogprobs() {
            return avgLogprobs;
        }
        public void setAvgLogprobs(double avgLogprobs) {
            this.avgLogprobs = avgLogprobs;
        }
    }

    public static class Content {
        private List<Part> parts;
        private String role;

        public List<Part> getParts() {
            return parts;
        }
        public void setParts(List<Part> parts) {
            this.parts = parts;
        }
        public String getRole() {
            return role;
        }
        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class Part {
        private String text;

        public String getText() {
            return text;
        }
        public void setText(String text) {
            this.text = text;
        }
    }

    public static class UsageMetadata {
        private int promptTokenCount;
        private int candidatesTokenCount;
        private int totalTokenCount;
        private List<TokenDetails> promptTokensDetails;
        private List<TokenDetails> candidatesTokensDetails;

        public int getPromptTokenCount() {
            return promptTokenCount;
        }
        public void setPromptTokenCount(int promptTokenCount) {
            this.promptTokenCount = promptTokenCount;
        }
        public int getCandidatesTokenCount() {
            return candidatesTokenCount;
        }
        public void setCandidatesTokenCount(int candidatesTokenCount) {
            this.candidatesTokenCount = candidatesTokenCount;
        }
        public int getTotalTokenCount() {
            return totalTokenCount;
        }
        public void setTotalTokenCount(int totalTokenCount) {
            this.totalTokenCount = totalTokenCount;
        }
        public List<TokenDetails> getPromptTokensDetails() {
            return promptTokensDetails;
        }
        public void setPromptTokensDetails(List<TokenDetails> promptTokensDetails) {
            this.promptTokensDetails = promptTokensDetails;
        }
        public List<TokenDetails> getCandidatesTokensDetails() {
            return candidatesTokensDetails;
        }
        public void setCandidatesTokensDetails(List<TokenDetails> candidatesTokensDetails) {
            this.candidatesTokensDetails = candidatesTokensDetails;
        }
    }

    public static class TokenDetails {
        private String modality;
        private int tokenCount;

        public String getModality() {
            return modality;
        }
        public void setModality(String modality) {
            this.modality = modality;
        }
        public int getTokenCount() {
            return tokenCount;
        }
        public void setTokenCount(int tokenCount) {
            this.tokenCount = tokenCount;
        }
    }
}