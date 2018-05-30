package com.github.viktorgozhiy.tipcalc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sbMinusBtn, sbPlusBtn, tipMinusBtn, tipPlusBtn;
    private TextView sbTv, tipTv,tppTv, ttTv, ttppTv, ttpTv;
    private EditText totalAmountEd;
    private CheckBox checkBox;
    private int splitBill, tip;
    private float totalAmount, tipPerPerson, totalTip, totalTipPerPerson, totalToPay;
    private boolean roundingPrices = false;
    private SharedPreferences sPref;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id) {
            case R.id.exit:
                this.finish();
                break;
            case R.id.about:
                showDialog(getResources().getString(R.string.about), getResources().getString(R.string.about_message));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getResources().getString(R.string.app_name));
        loadPreferences();
        totalAmount = 0;

        sbMinusBtn = (Button) findViewById(R.id.sbMinusBtn);
        sbPlusBtn = (Button) findViewById(R.id.sbPlusBtn);
        tipMinusBtn = (Button) findViewById(R.id.tipMinusBtn);
        tipPlusBtn = (Button) findViewById(R.id.tipPlusBtn);

        sbTv = (TextView) findViewById(R.id.sbTv);
        tipTv = (TextView) findViewById(R.id.tipTv);
        tppTv = (TextView) findViewById(R.id.tppTv);
        ttTv = (TextView) findViewById(R.id.ttTv);
        ttppTv = (TextView) findViewById(R.id.ttppTv);
        ttpTv = (TextView) findViewById(R.id.ttpTv);

        totalAmountEd = (EditText) findViewById(R.id.totalAmountEd);
        totalAmountEd.setText(Float.toString(totalAmount));

        checkBox = (CheckBox) findViewById(R.id.checkBox);

        sbMinusBtn.setOnClickListener(this);
        sbPlusBtn.setOnClickListener(this);
        tipMinusBtn.setOnClickListener(this);
        tipPlusBtn.setOnClickListener(this);

        totalAmountEd.setText("");

        totalAmountEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                try {
                    totalAmount = Float.parseFloat(editable.toString());
                } catch (NumberFormatException ex) {
                    totalAmount = 0;
                }
                calc();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                roundingPrices = b;
                calc();
            }
        });

        calc();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.sbMinusBtn:
                if (splitBill > 1) {
                    splitBill --;
                    calc();
                }
                break;
            case R.id.sbPlusBtn:
                splitBill ++;
                calc();
                break;
            case R.id.tipMinusBtn:
                if (tip > 1) {
                    tip --;
                    calc();
                }
                break;
            case R.id.tipPlusBtn:
                tip ++;
                calc();
                break;
        }
    }
    private void calc() {
        checkBox.setChecked(roundingPrices);
        sbTv.setText(Integer.toString(splitBill));
        tipTv.setText(Integer.toString(tip) + "%");

        if (roundingPrices) tipPerPerson = (float) Math.ceil(((totalAmount * tip) / 100) / splitBill);
        else tipPerPerson = ((totalAmount * tip) / 100) / splitBill;
        tppTv.setText(getResources().getString(R.string.currency_sign) + String.format(Locale.US, "%.2f", tipPerPerson));

        if (roundingPrices) totalTip = (float) Math.ceil(totalAmount * tip / 100);
        else totalTip = totalAmount * tip / 100;
        ttTv.setText(getResources().getString(R.string.currency_sign) + String.format(Locale.US, "%.2f", totalTip));

        if (roundingPrices) totalTipPerPerson = (float) Math.ceil((totalAmount + (totalAmount * tip / 100)) / splitBill);
        else totalTipPerPerson = (totalAmount + (totalAmount * tip / 100)) / splitBill;
        ttppTv.setText(getResources().getString(R.string.currency_sign) + String.format(Locale.US, "%.2f", totalTipPerPerson));

        if(roundingPrices) totalToPay = (float) Math.ceil(totalAmount + totalTip);
        else totalToPay = totalAmount + totalTip;
        ttpTv.setText(getResources().getString(R.string.currency_sign) + String.format(Locale.US, "%.2f", totalToPay));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        savePreferences();
    }

    private void savePreferences() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("splitBill", splitBill);
        ed.putInt("tip", tip);
        ed.putBoolean("roundingPrices", roundingPrices);
        ed.commit();
    }

    private void loadPreferences() {
        sPref = getPreferences(MODE_PRIVATE);
        splitBill = sPref.getInt("splitBill", 3);
        tip = sPref.getInt("tip", 10);
        roundingPrices = sPref.getBoolean("roundingPrices", false);
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
