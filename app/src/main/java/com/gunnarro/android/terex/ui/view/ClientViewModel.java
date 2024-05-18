package com.gunnarro.android.terex.ui.view;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.service.ClientService;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 */
public class ClientViewModel extends AndroidViewModel {

    private final ClientService clientService;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    private final MutableLiveData<List<ClientDto>> clientsLiveData;

    public ClientViewModel(Application application) {
        super(application);
        clientsLiveData = new MutableLiveData<>();
        clientService = new ClientService();
        clientsLiveData.setValue(clientService.getClients());
    }

    public LiveData<List<ClientDto>> getAllClients() {
        return clientsLiveData;
    }

}
