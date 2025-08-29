package org.apache.fineract.portfolio.savings;


import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.UriInfo;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.savings.api.SavingsAccountsApiResource;
import org.apache.fineract.portfolio.savings.api.SavingsApiSetConstants;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;



@ExtendWith(MockitoExtension.class)
public class SavingsAccountsApiResourceTest {
    @Mock
    private SavingsAccountReadPlatformService savingsAccountReadPlatformService;

    @Mock
    private PlatformSecurityContext context;

    @Mock
    private DefaultToApiJsonSerializer<SavingsAccountData> toApiJsonSerializer;

    @Mock
    private ApiRequestParameterHelper apiRequestParameterHelper;

    @Mock
    private UriInfo uriInfo;

    @Mock
    private AppUser appUser;

    @Mock
    private ApiRequestJsonSerializationSettings apiRequestJsonSerializationSettings;


    private SavingsAccountsApiResource savingsAccountsApiResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        savingsAccountsApiResource = new SavingsAccountsApiResource(
                savingsAccountReadPlatformService,
                context,
                toApiJsonSerializer,
                null,
                apiRequestParameterHelper,
                null,
                null,
                null
        );

    }

    @Test
    void retrieveAll_withValidDateOfBirth_shouldPassValidation() {
        doReturn(appUser).when(context).authenticatedUser();
        doReturn(new MultivaluedHashMap<String, String>()).when(uriInfo).getQueryParameters();
        doReturn(apiRequestJsonSerializationSettings).when(apiRequestParameterHelper).process(any());
        doReturn("{serialized}")
                .when(toApiJsonSerializer)
                .serialize(eq(apiRequestJsonSerializationSettings),
                        any(Page.class),
                        eq(SavingsApiSetConstants.SAVINGS_ACCOUNT_RESPONSE_DATA_PARAMETERS));
        Page<SavingsAccountData> page = new Page<>(Collections.emptyList(), 0);
        doReturn(page).when(savingsAccountReadPlatformService).retrieveAll(any(SearchParameters.class));

        String validDob = "15 March 1990";

        String result = savingsAccountsApiResource.retrieveAll(uriInfo, null, null, null, null, null, null, validDob);

        assertEquals("{serialized}", result);
    }


    @Test
    void retrieveAll_withInvalidDateOfBirth_shouldThrowException() {
        doReturn(appUser).when(context).authenticatedUser();
        assertThrows(IllegalArgumentException.class, () -> {
            savingsAccountsApiResource.retrieveAll(uriInfo, null, null, null, null, null, null, "1990-03-15");
        });

        verify(savingsAccountReadPlatformService, never()).retrieveAll(any());
    }

    @Test
    void retrieveAll_withNullDateOfBirth_shouldSkipValidation() {
        doReturn(appUser).when(context).authenticatedUser();
        doReturn(new MultivaluedHashMap<String, String>()).when(uriInfo).getQueryParameters();
        doReturn(apiRequestJsonSerializationSettings).when(apiRequestParameterHelper).process(any());
        doReturn("{serialized}")
                .when(toApiJsonSerializer)
                .serialize(eq(apiRequestJsonSerializationSettings),
                        any(Page.class),
                        eq(SavingsApiSetConstants.SAVINGS_ACCOUNT_RESPONSE_DATA_PARAMETERS));
        Page<SavingsAccountData> page = new Page<>(Collections.emptyList(), 0);
        doReturn(page).when(savingsAccountReadPlatformService).retrieveAll(any(SearchParameters.class));

        String result = savingsAccountsApiResource.retrieveAll(uriInfo, null, null, null, null, null, null, null);

        assertEquals("{serialized}", result);
        verify(savingsAccountReadPlatformService, times(1)).retrieveAll(any(SearchParameters.class));
    }

}
