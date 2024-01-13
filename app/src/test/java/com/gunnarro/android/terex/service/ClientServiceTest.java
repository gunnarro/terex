package com.gunnarro.android.terex.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    private ClientService clientService;

    @BeforeEach
    public void setup() {
        clientService = new ClientService();
    }


    @Test
    void saveClient() {
       ClientDto clientDto = new ClientDto();
/*
        List<TimesheetSummary> timesheetSummaries = List.of(timesheetSummaryWeek1);
        Long timesheetId = 1L;
        when(timesheetServiceMock.createTimesheetSummaryForBilling(anyLong())).thenReturn(TimesheetMapper.toTimesheetSummaryDtoList(timesheetSummaries));
        when(invoiceRepositoryMock.saveInvoice(any())).thenReturn(23L);
        Long invoiceId = invoiceService.createInvoice(timesheetId );
        assertEquals(23, invoiceId);*/
    }


}
