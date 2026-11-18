package az.iktex.organization.structure.testders.config;

import az.iktex.organization.structure.testders.dto.response.ErrorResponse;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME =
            "bearerAuth";

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()

                .info(new Info()
                        .title("School API")
                        .version("v1")
                        .description(
                                "School API - JWT Authentication"
                        )
                )

                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
                                        new SecurityScheme()
                                                .name(SECURITY_SCHEME_NAME)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )

                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(SECURITY_SCHEME_NAME)
                );
    }

    @Bean
    public OpenApiCustomizer securityResponsesCustomizer() {

        return openApi -> {

            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }

            ModelConverters.getInstance()
                    .read(ErrorResponse.class)
                    .forEach(
                            openApi.getComponents()::addSchemas
                    );

            if (openApi.getPaths() == null) {
                return;
            }

            openApi.getPaths().forEach((path, pathItem) -> {

                // Acıq endpoint-ler
                if (isPublicEndpoint(path)) {
                    return;
                }

                pathItem.readOperations().forEach(operation -> {

                    if (operation.getSecurity() != null
                            && operation.getSecurity().isEmpty()) {
                        return;
                    }

                    ApiResponses responses =
                            operation.getResponses();

                    if (responses == null) {

                        responses = new ApiResponses();

                        operation.setResponses(responses);
                    }

                    responses.putIfAbsent(
                            "401",
                            errorResponse(
                                    "Authentication is required"
                            )
                    );

                    responses.putIfAbsent(
                            "403",
                            errorResponse(
                                    "Access denied"
                            )
                    );
                });
            });
        };
    }

    private boolean isPublicEndpoint(String path) {

        return path.equals("/api/auth/register")
                || path.equals("/api/auth/login")
                || path.equals("/api/auth/refresh");
    }

    private ApiResponse errorResponse(String description) {

        return new ApiResponse()

                .description(description)

                .content(
                        new Content().addMediaType(
                                "application/json",
                                new MediaType().schema(
                                        new Schema<>()
                                                .$ref(
                                                        "#/components/schemas/ErrorResponse"
                                                )
                                )
                        )
                );
    }
}