package tt.hashtranslator.client.md5gromweb;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import tt.hashtranslator.exception.MD5DecoderServiceException;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
public class MD5DecryptErrorDecoder implements ErrorDecoder {
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
            case 429:
                log.error(errorMessage);
                return new MD5DecoderServiceException(errorMessage);
            default:
                return errorDecoder.decode(s, response);
        }
    }
}
