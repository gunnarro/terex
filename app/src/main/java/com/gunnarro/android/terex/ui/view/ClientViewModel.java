package com.gunnarro.android.terex.ui.view;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.repository.ClientRepository;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 */
public class ClientViewModel extends AndroidViewModel {

    private final ClientRepository clientRepository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    private final LiveData<List<Client>> clients;

    public ClientViewModel(Application application) {
        super(application);
        clientRepository = new ClientRepository();
        clients = clientRepository.getAllClients();
    }

    public LiveData<List<Client>> getAllClients() {
        return clients;
    }

}
