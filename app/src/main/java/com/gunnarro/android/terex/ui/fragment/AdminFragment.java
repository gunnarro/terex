package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.service.ClientService;
import com.gunnarro.android.terex.service.ProjectService;
import com.gunnarro.android.terex.service.TimesheetService;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminFragment extends BaseFragment {

    private NavController navController;

    private TimesheetService timesheetService;
    private ClientService clientService;
    private ProjectService projectService;

    @Inject
    public AdminFragment() {
        // Needed by dagger framework
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        timesheetService = new TimesheetService();
        clientService = new ClientService();
        projectService = new ProjectService();
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.title_admin);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        view.findViewById(R.id.btn_integration).setOnClickListener(v -> {
            navController.navigate(R.id.nav_from_admin_to_integration_list);
        });

        view.findViewById(R.id.btn_accounting).setOnClickListener(v -> {
            navController.navigate(R.id.accounting_fragment);
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

    // ---------------------------------------------------
    // adds a bunch of timesheet, only for load test
    // ---------------------------------------------------
    private void generateTestData() {
        // create a client
        ClientDto clientDto = new ClientDto(null);
        clientDto.setName("gunnarro sandbox");
        clientDto.setStatus(Client.ClientStatusEnum.ACTIVE.name());
        Long clientId = clientService.saveClient(clientDto);

        ProjectDto projectDto1 = new ProjectDto();
        projectDto1.setClientDto(new ClientDto(clientId));
        projectDto1.setName("terex load test");
        projectDto1.setStatus(Project.ProjectStatusEnum.ACTIVE.name());
        projectDto1.setHourlyRate(990);
        Long projectId1 = projectService.saveProject(projectDto1);

        ProjectDto projectDto2 = new ProjectDto();
        projectDto2.setClientDto(new ClientDto(clientId));
        projectDto2.setName("terex functional testing");
        projectDto2.setStatus(Project.ProjectStatusEnum.ACTIVE.name());
        projectDto2.setHourlyRate(1100);
        Long projectId2 = projectService.saveProject(projectDto2);

        Long userId = 1L;

        int year = 2024;
        for (int month = 1; month < 12; month++) {
            generateTimesheet(userId, projectDto1.getClientDto().getId(), projectDto1.getId(), year, month);
            generateTimesheet(userId, projectDto2.getClientDto().getId(), projectDto2.getId(), year, month);
        }
    }

    private void generateTimesheet(Long userId, Long clientId, Long projectId, Integer year, Integer month) {
        Timesheet newTimesheet = Timesheet.createDefault(userId, clientId, year, month);
        Long timesheetId = timesheetService.saveTimesheet(newTimesheet);
        List<TimesheetEntry> timesheetEntryList = createTimesheetEntriesForMonth(timesheetId, year, month);
        timesheetEntryList.forEach(e -> timesheetService.saveTimesheetEntry(e));
    }

    public static List<TimesheetEntry> createTimesheetEntriesForMonth(Long timesheetId, Integer year, Integer month) {
        List<TimesheetEntry> timesheetEntryList = new ArrayList<>();
        // get timesheet
        Timesheet timesheet = new Timesheet();
        timesheet.setFromDate(LocalDate.of(year, month, 1));
        timesheet.setToDate(LocalDate.of(year, month, Utility.getLastDayOfMonth(LocalDate.of(year, month, 1)).getDayOfMonth()));
        // interpolate missing days in the month.
        TimesheetEntry timesheetEntry = new TimesheetEntry();
        timesheetEntry.setTimesheetId(timesheetId);
        for (LocalDate date = timesheet.getFromDate(); date.isBefore(timesheet.getToDate()); date = date.plusDays(1)) {
            boolean isInList = false;
            for (TimesheetEntry e : timesheetEntryList) {
                if (e.getWorkdayDate().equals(date)) {
                    isInList = true;
                    break;
                }
            }
            if (!isInList) {
                TimesheetEntry timesheetEntry1 = new TimesheetEntry();
                timesheetEntry1.setTimesheetId(timesheetId);
                timesheetEntry1.setStatus(TimesheetEntry.TimesheetEntryStatusEnum.OPEN.name());
                timesheetEntry1.setType(TimesheetEntry.TimesheetEntryTypeEnum.REGULAR.name());
                timesheetEntry1.setWorkdayDate(date);
                timesheetEntry1.setStartTime(LocalTime.of(7, 0));
                timesheetEntry1.setWorkedSeconds((long) 420 * 60);
                timesheetEntry1.setWorkdayWeek(date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
                timesheetEntryList.add(timesheetEntry1);
            }
        }
        timesheetEntryList.sort(Comparator.comparing(TimesheetEntry::getWorkdayDate));
        return timesheetEntryList;
    }
}
