package tt.hashtranslator.client.authorization;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import tt.hashtranslator.exception.AuthServiceException;

import java.io.IOException;
import java.nio.charset.Charset;

public class AuthorizationErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {
        String errorMessage;
        try {
            errorMessage = Util.toString(response.body().asReader(Charset.defaultCharset()));
        } catch (IOException e) {
            errorMessage = "Can not read response body";
        }
        switch (response.status()) {
            case 400:
                return new AuthServiceException(errorMessage);
            default:
                return errorDecoder.decode(s, response);
        }
    }
}
