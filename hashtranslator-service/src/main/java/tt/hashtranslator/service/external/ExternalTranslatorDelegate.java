package tt.hashtranslator.service.external;

import tt.hashtranslator.data.Application;
import tt.hashtranslator.exception.ExternalTranslatorException;

public interface ExternalTranslatorDelegate {
    Application translate(Application application) throws ExternalTranslatorException;
}
