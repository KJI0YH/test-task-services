package tt.hashtranslator.client.md5decrypt;

import tt.hashtranslator.entity.Application;
import tt.hashtranslator.exception.ExternalTranslatorException;

public interface ExternalTranslatorDelegate {
    Application translate(Application application) throws ExternalTranslatorException;
}
