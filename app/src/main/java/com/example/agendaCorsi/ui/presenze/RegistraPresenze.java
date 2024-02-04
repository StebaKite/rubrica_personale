package com.example.agendaCorsi.ui.presenze;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.agendaCorsi.AgendaCorsiApp;
import com.example.agendaCorsi.MainActivity;
import com.example.agendaCorsi.database.access.ContattiDAO;
import com.example.agendaCorsi.database.access.ElementoPortfolioDAO;
import com.example.agendaCorsi.database.access.IscrizioneDAO;
import com.example.agendaCorsi.database.table.ContattoIscritto;
import com.example.agendaCorsi.database.table.ElementoPortfolio;
import com.example.agendaCorsi.database.table.Iscrizione;
import com.example.agendaCorsi.ui.base.FunctionBase;
import com.example.agendaCorsi.ui.base.QueryComposer;
import com.example.agendacorsi.R;

import java.util.List;
import java.util.Map;

public class RegistraPresenze extends FunctionBase {

    String idFascia, idCorso, descrizioneCorso, giornoSettimana, descrizioneFascia, sport, statoCorso, tipoCorso;
    EditText _descrizioneCorso, _descrizioneFascia, _giornoSettimana;
    TableLayout tabellaContattiIscritti, headerTabellaContattiIscritti;
    Context registraPresenze;

    int larghezzaColonna1, larghezzaColonna2, larghezzaColonna3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registra_presenze);

        registraPresenze = this;
        makeToolBar(registraPresenze);

        esci = findViewById(R.id.bExit);

        Intent intent = getIntent();
        idFascia = intent.getStringExtra("idFascia");
        descrizioneCorso = intent.getStringExtra("descrizioneCorso");
        descrizioneFascia = intent.getStringExtra("descrizioneFascia");
        giornoSettimana = intent.getStringExtra("giornoSettimana");
        sport = intent.getStringExtra("sport");
        idCorso = intent.getStringExtra("idCorso");
        statoCorso = intent.getStringExtra("statoCorso");
        tipoCorso = intent.getStringExtra("tipoCorso");

        _descrizioneCorso = findViewById(R.id.editDescrizione);
        _giornoSettimana = findViewById(R.id.editGiornoSettimana);
        _descrizioneFascia = findViewById(R.id.editFascia);

        headerTabellaContattiIscritti = findViewById(R.id.headerTabellaContattiIscrivibili);
        tabellaContattiIscritti = findViewById(R.id.tabellaContattiIscritti);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        larghezzaColonna1 = (int) (displayMetrics.widthPixels * 0.4);
        larghezzaColonna2 = (int) (displayMetrics.widthPixels * 0.1);
        larghezzaColonna3 = (int) (displayMetrics.widthPixels * 0.2);

        _descrizioneCorso.setText(descrizioneCorso);
        _giornoSettimana.setText(giornoSettimana);
        _descrizioneFascia.setText(descrizioneFascia);

        makeIntestazioneTabella();
        loadContattiIscritti();

        listenerEsci(AgendaCorsiApp.getContext(), ElencoFasceCorsiRunning.class, null);
    }


    private void loadContattiIscritti() {

        List<Object> contattiIscrittiList = ContattiDAO.getInstance().getIscrittiRunning(idFascia, QueryComposer.getInstance().getQuery(QUERY_GET_CONTATTI_ISCRITTI_RUNNING));

        for (Object object : contattiIscrittiList) {
            ContattoIscritto contattoIscritto = (ContattoIscritto) object;

            String detailType = "";
            if (contattoIscritto.getStato().equals(STATO_CHIUSO) || contattoIscritto.getStatoElemento().equals(STATO_ESAURITO)) {
                detailType = DETAIL_CLOSED;
            } else {
                detailType = (contattoIscritto.getIdPresenza().equals("")) ? DETAIL_SIMPLE : DETAIL_CONFIRMED;
            }

            tableRow = new TableRow(this);
            tableRow.setClickable(true);
            tableRow.addView(makeCell(this, new TextView(this), detailType, larghezzaColonna1, contattoIscritto.getNomeContatto(), View.TEXT_ALIGNMENT_TEXT_START, View.VISIBLE));
            tableRow.addView(makeCell(this, new TextView(this), detailType, larghezzaColonna2, String.valueOf(computeAge(contattoIscritto.getDataNascita())), View.TEXT_ALIGNMENT_TEXT_START, View.VISIBLE));
            tableRow.addView(makeCell(this, new TextView(this), detailType, larghezzaColonna3, contattoIscritto.getStato(), View.TEXT_ALIGNMENT_TEXT_START, View.VISIBLE));
            tableRow.addView(makeCell(this,new TextView(this), DETAIL, 0, contattoIscritto.getIdElemento(), 0, View.GONE));
            tableRow.addView(makeCell(this,new TextView(this), DETAIL, 0, contattoIscritto.getIdPresenza(), 0, View.GONE));
            tableRow.addView(makeCell(this,new TextView(this), DETAIL, 0, contattoIscritto.getIdIscrizione(), 0, View.GONE));

            Map<String, String> intentMap = new ArrayMap<>();
            intentMap.put("descrizioneCorso", descrizioneCorso);
            intentMap.put("descrizioneFascia", descrizioneFascia);
            intentMap.put("giornoSettimana", giornoSettimana);
            intentMap.put("sport", sport);
            intentMap.put("idCorso", idCorso);
            intentMap.put("idFascia", idFascia);
            intentMap.put("idIscrizione", contattoIscritto.getIdIscrizione());
            intentMap.put("statoCorso", statoCorso);
            intentMap.put("nomeIscritto", contattoIscritto.getNomeContatto());
            intentMap.put("statoIscrizione", contattoIscritto.getStato());
            intentMap.put("tipoCorso", tipoCorso);
            intentMap.put("idElemento", contattoIscritto.getIdElemento());

            if (contattoIscritto.getStato().equals(STATO_ATTIVA) && contattoIscritto.getStatoElemento().equals(STATO_CARICO)) {

                tableRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TableRow tableRow = (TableRow) view;
                        TextView textView = (TextView) tableRow.getChildAt(3);
                        String idElementoSelezionato = textView.getText().toString();

                        tableRow.setBackground(ContextCompat.getDrawable(registraPresenze, R.drawable.cell_bg_gradient));

                        textView = (TextView) tableRow.getChildAt(4);
                        String idPresenzaSelezionato = textView.getText().toString();

                        textView = (TextView) tableRow.getChildAt(5);
                        String idIscrizioneSelezionato = textView.getText().toString();

                        if (idPresenzaSelezionato.equals("")) {
                            if (CreaPresenzaContattoIscritto.getInstance().make(idIscrizioneSelezionato,idElementoSelezionato)) {
                                ElementoPortfolio elementoPortfolio = new ElementoPortfolio(idElementoSelezionato, null, null, null, null, null, null);
                                ElementoPortfolioDAO.getInstance().select(elementoPortfolio, QueryComposer.getInstance().getQuery(QUERY_GET_ELEMENTO));
                                if (!elementoPortfolio.getIdElemento().equals("")) {    // e' strano ma non lo trovo l'elemento on questo id
                                    if (Integer.parseInt(elementoPortfolio.getNumeroLezioni()) == 0) {
                                        Iscrizione iscrizione = new Iscrizione(idIscrizioneSelezionato, null, null, STATO_DISATTIVA, null, null);
                                        IscrizioneDAO.getInstance().updateStato(iscrizione, QueryComposer.getInstance().getQuery(QUERY_MOD_STATO_ISCRIZIONE));
                                        makeToastMessage(registraPresenze, "Presenza confermata, " + contattoIscritto.getNomeContatto() + " ha terminato le lezioni").show();
                                    } else {
                                        makeToastMessage(registraPresenze, "Presenza confermata, " + elementoPortfolio.getNumeroLezioni() + " lezioni rimanenti").show();
                                    }
                                }
                            }
                        } else {
                            if (RimuoviPresenzaContattoIscritto.getInstance().make(idPresenzaSelezionato,idElementoSelezionato)) {
                                ElementoPortfolio elementoPortfolio = new ElementoPortfolio(idElementoSelezionato, null, null, null, null, null, null);
                                ElementoPortfolioDAO.getInstance().select(elementoPortfolio, QueryComposer.getInstance().getQuery(QUERY_GET_ELEMENTO));
                                if (!elementoPortfolio.getIdElemento().equals("")) {    // e' strano ma non lo trovo l'elemento on questo id
                                    makeToastMessage(registraPresenze, "Presenza rimossa, " + elementoPortfolio.getNumeroLezioni() + " lezioni rimanenti").show();
                                }
                            }
                        }

                        Intent intent = getIntent();

                        intentMap.put("idElemento", idElementoSelezionato);
                        intentMap.put("idPresenza", idPresenzaSelezionato);
                        intentMap.put("idIscrizione", idIscrizioneSelezionato);

                        if (intentMap != null) {
                            for (Map.Entry<String, String> entry : intentMap.entrySet()) {
                                intent.putExtra(entry.getKey(), entry.getValue());
                            }
                        }
                        startActivity(intent);
                        finish();
                    }
                });
            }
            tabellaContattiIscritti.addView(tableRow);
        }
    }

    private void makeIntestazioneTabella() {
        tableRow = new TableRow(registraPresenze);
        tableRow.setClickable(false);
        tableRow.addView(makeCell(this,new TextView(this), HEADER, larghezzaColonna1,"Nome", View.TEXT_ALIGNMENT_TEXT_START, View.VISIBLE));
        tableRow.addView(makeCell(this,new TextView(this), HEADER, larghezzaColonna2,"Età", View.TEXT_ALIGNMENT_TEXT_START, View.VISIBLE));
        tableRow.addView(makeCell(this,new TextView(this), HEADER, larghezzaColonna3,"Stato", View.TEXT_ALIGNMENT_TEXT_START, View.VISIBLE));
        headerTabellaContattiIscritti.addView(tableRow);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("Home")) {
            Intent intent = new Intent(RegistraPresenze.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
