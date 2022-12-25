package apiPOJO.exampleOnePojo.auth;

public enum AuthResources {

    login("/auth/login");

    private String resource;

    AuthResources(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

}
