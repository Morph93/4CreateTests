package apiPOJO.exampleOnePojo.secondLayerExample;

public enum ExampleResource {

    exampleResourceOne("/api/merchant-accounts/%s/vmpi-status"),
    exampleResourceTwo("/api/merchant-accounts?%s");

    private String resource;

    ExampleResource(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    public String getResource(String additionalResource) {
        return String.format(resource, additionalResource);
    }

}
