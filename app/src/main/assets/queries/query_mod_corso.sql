update #TABLENAME# set
    descrizione = '#DESC#',
    data_inizio_validita = '#DATINI#',
    data_fine_validita = '#DATFIN#',
    data_ultimo_aggiornamento = datetime('now')
 where id_corso = #IDCORSO#