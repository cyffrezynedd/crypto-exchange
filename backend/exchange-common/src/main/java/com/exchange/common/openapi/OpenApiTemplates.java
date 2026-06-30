package com.exchange.common.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

public final class OpenApiTemplates {

    private static final String BEARER_SCHEME = "bearerAuth";

    private OpenApiTemplates() {
    }

    public static OpenAPI forService(String title, String gatewayPrefix) {
        return base(title)
                .addServersItem(server(gatewayPrefix))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }

    public static OpenAPI forGateway() {
        return base("Crypto Exchange API Gateway")
                .addServersItem(server("/"));
    }

    private static OpenAPI base(String title) {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version("v1")
                        .description("Crypto Exchange REST API"))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME, bearerAuth()));
    }

    private static Server server(String url) {
        return new Server().url(url);
    }

    private static SecurityScheme bearerAuth() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Access token from POST /api/v1/iam/auth/login");
    }
}
