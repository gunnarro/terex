package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.UserAccountDto;
import com.gunnarro.android.terex.service.UserAccountService;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminFragment extends Fragment {

    private NavController navController;

    private UserAccountService userAccountService;

    @Inject
    public AdminFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_admin);
        setHasOptionsMenu(true);
        userAccountService = new UserAccountService();
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        UserAccountDto userAccountDto = userAccountService.getUserAccount(1L);
        if (userAccountDto != null) {
            ((TextView) view.findViewById(R.id.user_account_account_type_view)).setText(userAccountDto.getUserAccountType());
            ((TextView) view.findViewById(R.id.user_account_username_view)).setText(userAccountDto.getUserName());
            ((TextView) view.findViewById(R.id.user_account_organization_name)).setText(userAccountDto.getOrganizationDto().getName());
            ((TextView) view.findViewById(R.id.user_account_organization_number)).setText(userAccountDto.getOrganizationDto().getOrganizationNumber());
            ((TextView) view.findViewById(R.id.user_account_organization_bank_account)).setText(userAccountDto.getOrganizationDto().getBankAccountNumber());
            ((TextView) view.findViewById(R.id.user_account_organization_address)).setText(userAccountDto.getOrganizationDto().getBusinessAddress().getStreetAddress());
            ((TextView) view.findViewById(R.id.user_account_organization_city)).setText(String.format("%s %s", userAccountDto.getOrganizationDto().getBusinessAddress().getPostalCode(), userAccountDto.getOrganizationDto().getBusinessAddress().getCity()));
            ((TextView) view.findViewById(R.id.user_account_organization_country)).setText(userAccountDto.getOrganizationDto().getBusinessAddress().getCountry());
        }


        view.findViewById(R.id.btn_user_account).setOnClickListener(v -> {
            navController.navigate(R.id.nav_from_admin_to_user_account);
        });

        view.findViewById(R.id.btn_settings).setOnClickListener(v -> {
        });

        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    /**
     * Update backup info after view is successfully create
     */
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.navController = Navigation.findNavController(view);
    }

}
