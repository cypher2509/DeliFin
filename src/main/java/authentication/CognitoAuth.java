package authentication;

import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.HashMap;
import java.util.Map;

@RestController

public class CognitoAuth {

    // Replace these with your Cognito configuration
    private static final String USER_POOL_ID = "us-east-1_4ZifxHNwI"; // e.g., us-east-1_ABC123
    private static final String APP_CLIENT_ID = "1nrkdq5krfdhe3s47euac0rp3d"; // e.g., 1h2jk3lmnop4567qrstuv
    private static final String APP_CLIENT_SECRET = "enjnocku61a21fehuoh50aj5fur8m2ebfdt93d7ko84po70urn5"; // Only if a secret is enabled


    @PostMapping("/signin")
    public Map<String, String> signin(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        System.out.println("username: " + username + " password: " + password);
        CognitoIdentityProviderClient client = CognitoIdentityProviderClient.create();
        try {
            // Calculate SECRET_HASH if App Client has a secret
            String secretHash = SecretHashCalculator.calculateSecretHash(APP_CLIENT_ID, APP_CLIENT_SECRET, username);

            // Build the sign-in request
            AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                    .userPoolId(USER_POOL_ID)
                    .clientId(APP_CLIENT_ID)
                    .authParameters(
                            Map.of(
                                    "USERNAME", username,
                                    "PASSWORD", password,
                                    "SECRET_HASH", secretHash
                            )
                    )
                    .build();

            // Send the sign-in request
            AdminInitiateAuthResponse authResponse = client.adminInitiateAuth(authRequest);

            // Extract tokens and return them
            AuthenticationResultType authResult = authResponse.authenticationResult();
            Map<String, String> response = new HashMap<>();
            response.put("idToken", authResult.idToken());
            response.put("accessToken", authResult.accessToken());
            response.put("refreshToken", authResult.refreshToken());
            return response;

        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("Sign-in failed: " + e.awsErrorDetails().errorMessage());
        } finally {
            client.close();
        }
    }


}

