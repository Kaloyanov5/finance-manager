package spring.project.finance_manager.component;

import java.util.List;

public class CohereApiResponse {
    private String id;
    private List<Generation> generations;
    private String prompt;
    private Meta meta;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Generation> getGenerations() {
        return generations;
    }

    public void setGenerations(List<Generation> generations) {
        this.generations = generations;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public static class Generation {
        private String id;
        private String text;
        private String finish_reason;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getFinish_reason() {
            return finish_reason;
        }

        public void setFinish_reason(String finish_reason) {
            this.finish_reason = finish_reason;
        }
    }

    public static class Meta {
        private ApiVersion api_version;
        private BilledUnits billed_units;

        public ApiVersion getApi_version() {
            return api_version;
        }

        public void setApi_version(ApiVersion api_version) {
            this.api_version = api_version;
        }

        public BilledUnits getBilled_units() {
            return billed_units;
        }

        public void setBilled_units(BilledUnits billed_units) {
            this.billed_units = billed_units;
        }

        public static class ApiVersion {
            private String version;

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }
        }

        public static class BilledUnits {
            private int input_tokens;
            private int output_tokens;

            public int getInput_tokens() {
                return input_tokens;
            }

            public void setInput_tokens(int input_tokens) {
                this.input_tokens = input_tokens;
            }

            public int getOutput_tokens() {
                return output_tokens;
            }

            public void setOutput_tokens(int output_tokens) {
                this.output_tokens = output_tokens;
            }
        }
    }
}
