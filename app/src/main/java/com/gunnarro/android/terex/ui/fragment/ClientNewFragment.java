package com.gunnarro.android.terex.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.BusinessAddressDto;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.integration.breg.BregService;
import com.gunnarro.android.terex.service.ClientService;
import com.gunnarro.android.terex.service.OrganizationService;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ClientNewFragment extends BaseFragment implements View.OnClickListener {

    private final BregService bregService;
    private ClientService clientService;
    private OrganizationService organizationService;

    @Inject
    public ClientNewFragment() {
        bregService = new BregService();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_client_new);
        clientService = new ClientService();
        organizationService = new OrganizationService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // do not show the action bar
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_client_new, container, false);

        view.findViewById(R.id.client_new_search_org_number_btn).setOnClickListener(v -> {
            lookupOrgNumber();
        });

        // disable save button as default
        view.findViewById(R.id.client_new_save_btn).setEnabled(true);
        view.findViewById(R.id.client_new_save_btn).setOnClickListener(v -> {
            if (!isInputDataValid()) {
                showInfoDialog("INFO", "Invalid input data! Please check!");
                return;
            }
            view.findViewById(R.id.client_new_save_btn).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            saveClient();
            navigateTo(R.id.nav_from_client_new_to_client_list, null);
        });

        view.findViewById(R.id.client_new_cancel_btn).setOnClickListener(v -> {
            view.findViewById(R.id.client_new_cancel_btn).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            // Simply return back to home page
            navigateTo(R.id.nav_from_client_new_to_client_list, null);
        });
        return view;
    }

    /**
     * lookup org number in external register, brønnøysund register
     */
    private void lookupOrgNumber() {
        // clear current data
        clearOrganizationSearchResult();
        String orgNr = ((TextView) requireView().findViewById(R.id.client_new_search_org_number)).getText().toString();
        if (!orgNr.isBlank()) {
            OrganizationDto organizationDto = bregService.findOrganization(orgNr);
            if (organizationDto != null) {
                updateOrganizationInputData(requireView(), organizationDto);
            } else {
                showInfoDialog("INFO", "Nothing found for organization number: " + orgNr);
            }
        } else {
            showInfoDialog("INFO", "Please type a valid organization number!");
        }
    }

    private void saveClient() {
        try {
            ClientDto clientDto = readClientInputData();
            Long organizationId = organizationService.save(clientDto.getOrganizationDto());
            clientDto.getOrganizationDto().setId(organizationId);
            clientService.saveClient(clientDto);
            showSnackbar(String.format(getResources().getString(R.string.info_client_saved_msg_format), clientDto.getOrganizationDto().getOrganizationNumber(), clientDto.getOrganizationDto().getName()), R.color.color_snackbar_text_add);
        } catch (TerexApplicationException | InputValidationException ex) {
            ex.printStackTrace();
            showInfoDialog("Error", String.format("%s", ex.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            showInfoDialog("Error", String.format("%s", e.getCause()));
        }
    }

    private void updateOrganizationInputData(@NotNull View view, @NotNull OrganizationDto organizationDto) {
        // new client do not have id yet, the id is generated upon save to database
        if (organizationDto.getId() != null) {
            ((TextView) view.findViewById(R.id.client_new_org_id)).setText(organizationDto.getId().toString());
        }
        ((TextView) view.findViewById(R.id.client_new_org_name)).setText(organizationDto.getName());
        ((TextView) view.findViewById(R.id.client_new_org_number)).setText(organizationDto.getOrganizationNumber());
        ((TextView) view.findViewById(R.id.client_new_org_street_name)).setText(organizationDto.getBusinessAddress().getAddress());
        ((TextView) view.findViewById(R.id.client_new_org_postal_code)).setText(organizationDto.getBusinessAddress().getPostalCode());
        ((TextView) view.findViewById(R.id.client_new_org_city_name)).setText(organizationDto.getBusinessAddress().getCity());
        ((TextView) view.findViewById(R.id.client_new_org_country)).setText(organizationDto.getBusinessAddress().getCountry());
    }

    private void clearOrganizationSearchResult() {
        OrganizationDto emptyOrg = new OrganizationDto();
        emptyOrg.setId(null);
        emptyOrg.setOrganizationNumber("");
        emptyOrg.setName("");
        BusinessAddressDto emptyAddr = new BusinessAddressDto();
        emptyAddr.setId(null);
        emptyAddr.setAddress("");
        emptyAddr.setCity("");
        emptyAddr.setPostalCode("");
        emptyAddr.setCountry("");
        emptyOrg.setBusinessAddress(emptyAddr);
        updateOrganizationInputData(requireView(), emptyOrg);
    }

    @Override
    public void onClick(View view) {
        // ask every time
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You have not been granted access, ask for permission now, no need for nay permissions
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, permission not granted");
        }

        int id = view.getId();
        if (id == R.id.client_new_save_btn) {
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, save client");
        } else if (id == R.id.client_new_cancel_btn) {
            // return back to main view
            Log.d(Utility.buildTag(getClass(), "onClick"), "cancel button, return back to client list view");
        }
    }

    private ClientDto readClientInputData() {
        ClientDto clientDto = new ClientDto();

        TextView clientIdView = requireView().findViewById(R.id.client_new_id);
        if (clientIdView.getText() != null && !clientIdView.getText().toString().isBlank()) {
            clientDto.setId(Long.parseLong(clientIdView.getText().toString()));
        }
/*
        EditText createdDateView = requireView().findViewById(R.id.client_new_created_date);
        LocalDateTime createdDateTime = Utility.toLocalDateTime(createdDateView.getText().toString());
        if (createdDateTime != null) {
            // clientDto.setCreatedDate(createdDateTime);
        }

        EditText lastModifiedDateView = requireView().findViewById(R.id.client_new_last_modified_date);
        LocalDateTime lastModifiedDateTime = Utility.toLocalDateTime(lastModifiedDateView.getText().toString());
        if (lastModifiedDateTime != null) {
            // clientDto.setLastModifiedDate(lastModifiedDateTime);
        }
*/
        OrganizationDto organizationDto = new OrganizationDto();
        TextView orgIdView = requireView().findViewById(R.id.client_new_org_id);
        if (orgIdView.getText() != null && !orgIdView.getText().toString().isBlank()) {
            organizationDto.setId(Long.parseLong(orgIdView.getText().toString()));
        }
        organizationDto.setOrganizationNumber(((TextView) requireView().findViewById(R.id.client_new_org_number)).getText().toString());
        organizationDto.setName(((TextView) requireView().findViewById(R.id.client_new_org_name)).getText().toString());

        BusinessAddressDto businessAddressDto = new BusinessAddressDto();
        TextView businessAddrIdView = requireView().findViewById(R.id.client_new_business_addr_id);
        if (businessAddrIdView.getText() != null && !businessAddrIdView.getText().toString().isBlank()) {
            businessAddressDto.setId(Long.parseLong(businessAddrIdView.getText().toString()));
        }
        businessAddressDto.setAddress(((TextView) requireView().findViewById(R.id.client_new_org_street_name)).getText().toString());
        businessAddressDto.setPostalCode(((TextView) requireView().findViewById(R.id.client_new_org_postal_code)).getText().toString());
        businessAddressDto.setCity(((TextView) requireView().findViewById(R.id.client_new_org_city_name)).getText().toString());
        businessAddressDto.setCountry(((TextView) requireView().findViewById(R.id.client_new_org_country)).getText().toString());

        /*
        ContactInfoDto orgContactInfoDto = new ContactInfoDto();
        TextView contactInfoIdView = requireView().findViewById(R.id.client_new_contact_info_id);
        if (contactInfoIdView.getText() != null && !contactInfoIdView.getText().toString().isBlank()) {
            orgContactInfoDto.setId(Long.parseLong(contactInfoIdView.getText().toString()));
        }
        orgContactInfoDto.setEmailAddress(((TextView) requireView().findViewById(R.id.client_new_org_contact_email)).getText().toString());
        orgContactInfoDto.setMobileNumberCountryCode("+47");
        orgContactInfoDto.setMobileNumber(((TextView) requireView().findViewById(R.id.client_new_org_contact_mobile)).getText().toString());

        PersonDto contactPersonDto = new PersonDto();
        TextView contactPersonIdView = requireView().findViewById(R.id.client_new_contact_person_id);
        if (contactPersonIdView.getText() != null && !contactPersonIdView.getText().toString().isBlank()) {
            contactPersonDto.setId(Long.parseLong(contactPersonIdView.getText().toString()));
        }
        contactPersonDto.setFirstName(((TextView) requireView().findViewById(R.id.client_new_contact_person_full_name)).getText().toString());
        ContactInfoDto personContactInfo = new ContactInfoDto();
        personContactInfo.setEmailAddress(((TextView) requireView().findViewById(R.id.client_new_contact_person_email)).getText().toString());
        personContactInfo.setMobileNumberCountryCode("+47");
        personContactInfo.setMobileNumber(((TextView) requireView().findViewById(R.id.client_new_contact_person_mobile)).getText().toString());
        contactPersonDto.setContactInfo(personContactInfo);
*/
        organizationDto.setBusinessAddress(businessAddressDto);
  //      organizationDto.setContactInfo(orgContactInfoDto);
  //      organizationDto.setContactPerson(contactPersonDto);
        // name of client is set equal to organization name
        clientDto.setName(organizationDto.getName());
        clientDto.setOrganizationDto(organizationDto);
        return clientDto;
    }

    private boolean isInputDataValid() {
        return true;
    }

    private boolean hasText(Editable e) {
        return e != null && !e.toString().isBlank();
    }

    /**
     * Used for input validation
     */
    private TextWatcher createEmptyTextValidator(EditText editText, String regexp, String validationErrorMsg) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!editText.getText().toString().matches(regexp)) {
                    editText.setError(validationErrorMsg);
                }
            }
        };
    }
}
