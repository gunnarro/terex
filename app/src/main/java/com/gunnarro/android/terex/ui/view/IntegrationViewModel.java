package com.gunnarro.android.terex.ui.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.domain.entity.Integration;
import com.gunnarro.android.terex.service.IntegrationService;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 */
public class IntegrationViewModel extends AndroidViewModel {

    private final IntegrationService integrationService;

    public IntegrationViewModel(@NonNull Application application) {
        super(application);
        integrationService = new IntegrationService();
    }

    public LiveData<List<Integration>> getIntegrationsLiveData() {
        return integrationService.getIntegrationsLiveData();
    }

    public void save(Integration integration) {
        //integrationService.saveIntegration(integration);
    }
}
