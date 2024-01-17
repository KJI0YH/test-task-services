package tt.authorization.service.password


import spock.lang.Specification
import tt.authorization.exception.PasswordServiceException

class BCryptPasswordServiceSpec extends Specification {

    private PasswordService passwordService = new BCryptPasswordService();

    def "encode should hash the password #password"() {
        when:
        def hash = passwordService.encode(password);
        then:
        notThrown(PasswordServiceException)
        hash != null
        hash != password
        where:
        password << ["password", "another_password", "H@r6*8@%%w0rD", ""]
    }

    def "compare should not throw PasswordServiceException for matching passwords"() {
        given:
        def hash = passwordService.encode(password);
        when:
        passwordService.compare(password, hash);
        then:
        notThrown(PasswordServiceException)
        where:
        password << ["password", "another_password", "H@r6*8@%%w0rD", ""]
    }

    def "compare should throw PasswordServiceException for mismatched password"() {
        given:
        def hash = passwordService.encode(password);
        when:
        passwordService.compare(mismathedPassword, hash)
        then:
        thrown(PasswordServiceException)
        where:
        password   | mismathedPassword
        "password" | "mismatchedPassword"
        "qwerty"   | "qwertu"
        "simple"   | "hard"
    }
}