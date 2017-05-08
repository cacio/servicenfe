package br.com.prodasiq.leopardoa7printer.services;

import android.app.AlertDialog;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.UUID;

import br.com.prodasiq.leopardoa7printer.BoletoPrinter;
import br.com.prodasiq.leopardoa7printer.BoletoUtils;
import br.com.prodasiq.leopardoa7printer.NfePrinterA7;
import br.com.prodasiq.leopardoa7printer.ReceiptPrinterA7;
import br.com.prodasiq.leopardoa7printer.util.Constants;
import br.com.prodasiq.leopardoa7printer.util.FileHelper;


public class BluetoothPrintService extends IntentService implements Runnable{

    /*veriaveis do dedao*/
    private static final int REQUEST_ENABLE_BT = 1;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BluetoothDevice btDevice;
    private BluetoothSocket btSocket;

    private InputStream btInputStream;
    private OutputStream btOutputStream;

    /*variaveis do dedao*/
    private AlertDialog alerta;
    public  static Object jObject;
    public  static NfePrinterA7 nfeprinter;
    public  static BoletoPrinter boletoprinter;
    public  static ReceiptPrinterA7 receiptprinter;
    private static BoletoUtils boleto;
    private static int btntype = 0;
   // private static TextView status;
    private static boolean connected = false;

    public BluetoothPrintService() {
        super("BluetoothPrintService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /* Estanciando as classe */
        nfeprinter     = new NfePrinterA7();
        boletoprinter  = new BoletoPrinter();
        receiptprinter = new ReceiptPrinterA7();

        Bundle bundle = intent.getExtras();
        String pathFile = bundle.getString(Constants.PATH_KEY);

        int dot = pathFile.lastIndexOf(".");
        String ext = pathFile.substring(dot + 1);

        if(ext.equals("txt")) {

            genNotaTxt(pathFile);

        }else{

               // genBoletoByClassA72(pathFile);
                genBoletoResponse();
                //genReciboByClassA7();
        }

    }

    public void genNotaTxt(String pathFiles){

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            throw new UnsupportedOperationException();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            throw new UnsupportedOperationException();
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                if(device.getName().equals("MPT-III")) {
                    try {
                        btDevice = device;
                        btSocket = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.cancelDiscovery();

        try {

            btSocket.connect();
            btOutputStream = btSocket.getOutputStream();

            Thread.sleep(1000);

                String fileContent = FileHelper.getFileContent(pathFiles);
                byte[] fileBytes = FileHelper.getFileBytes(pathFiles);

                btOutputStream.write(fileBytes);
                btOutputStream.flush();

                btOutputStream.close();
                btSocket.close();

                File file = new File(pathFiles);
                file.delete();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void Canhoto(){



        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            throw new UnsupportedOperationException();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            throw new UnsupportedOperationException();
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                if(device.getName().equals("MPT-III")) {
                    try {
                        btDevice = device;
                        btSocket = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.cancelDiscovery();

        try {

            String xstring = "Recebi(emos) o bloqueto de cobrança com as\n" +
                             "características descritas neste comprovante de entrega. Ciente: __________________________________ _______/_______/_______\n" +
                             "Data do Documento 16/12/2016";

            btSocket.connect();
            btOutputStream = btSocket.getOutputStream();

            Thread.sleep(1000);

            btOutputStream.write(Integer.parseInt(xstring));
            btOutputStream.flush();

            btOutputStream.close();
            btSocket.close();



        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

   /* private static byte[] imgbanco(){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage imag = ImageIO.read(new File("C:\teste.bmp"));
        // 1 - 2 - 4 - 8 - 16 - 32 - 64 - 128
        byte valor = 0;
        int posBit = 8;
        for (int y = 0; y < imag.getHeight(); y++) {
            for (int x = 0; x < imag.getWidth(); x++) {
                Color c = new Color(imag.getRGB(x, y));
                posBit--;
                if (!(c.getRed() == 255 && c.getBlue() == 255 && c.getGreen() == 255)) {
                    valor += Math.pow(2, posBit);
                }
                if (posBit == 0) {
                    posBit = 8;
                    baos.write(new byte[]{valor});
                    valor = 0;
                }
            }
        }
        if (posBit != 8) {
            baos.write(new byte[]{valor});
        }
        byte[] bytes = baos.toByteArray();
        return bytes;
    }*/

    public void genBoletoByClassA72(JSONObject jObject) throws JSONException {
        connected = boletoprinter.connect(false);
        if (connected) {

            

            boleto = new BoletoUtils();
                boleto.setLinhaDigitavel(""+jObject.getString("linha_digitavel")+"");
               // boleto.setNumeroCodBarras(""+jObject.getString("codigo_barras")+"");
                boleto.setNomeBanco("Sicredi");
                boleto.setCodBanco(""+jObject.getString("codigo_banco_com_dv")+"");
                boleto.setLocalPagamento("Banco Sicredi Participações S.A.");
                boleto.setLocalOpcionalPagamento("Pagavel preferencialmente em qual quer agência Sicredi.");
                boleto.setVencimento(""+jObject.getString("data_vencimento")+"");
                boleto.setCedente(""+jObject.getString("cedente")+"");

            Toast.makeText(this,
                    ""+boleto.getCedente()+"",
                    Toast.LENGTH_LONG).show();

                boleto.setAgenciaCodigoCedente(""+jObject.getString("agencia_codigo")+"");
                boleto.setDatadocumento(""+jObject.getString("data_documento")+"");
                boleto.setNumeroDocumento(""+jObject.getString("numero_duplicata")+"");
                boleto.setEspecieDoc(""+jObject.getString("especie_doc")+"");
                boleto.setAceite(""+jObject.getString("aceite")+"");
                boleto.setDataProcessameto(""+jObject.getString("data_processamento")+"");
                boleto.setNossoNumero(""+jObject.getString("nosso_numero")+"");
                boleto.setUsoDoBanco(""+jObject.getString("usobanco")+"");
                boleto.setCip("000");
                boleto.setCarteira(""+jObject.getString("carteira")+"");
                boleto.setEspecieMoeda("R$");
                boleto.setQuantidade(""+jObject.getString("quantidade")+"");
                boleto.setValor("");
                boleto.setValorDocumento(""+jObject.getString("valor_boleto")+"");
                boleto.setInstrucoesCedente(new String[]{
                    ""+jObject.getString("identificacao1")+"",
                    ""+jObject.getString("identificacao2")+"", ""+jObject.getString("identificacao3")+"", "", "", "", ""});
                boleto.setDesconto("0,00");
                boleto.setDeducoes("0,00");
                boleto.setMulta("0,00");
                boleto.setAcrescimos("0,00");
                boleto.setValorCobrado(""+jObject.getString("valor_boleto")+"");
                boleto.setSacadoNome(""+jObject.getString("sacado")+"");
                boleto.setSacadoEndereco(""+jObject.getString("sacado_endereco")+"");
                boleto.setSacadoCep(""+jObject.getString("sacado_cep")+"");
                boleto.setSacadoCidade(""+jObject.getString("sacado_cidade")+"");
                boleto.setSacadoUF(""+jObject.getString("sacado_uf")+"");
                boleto.setSacadoCnpj(""+jObject.getString("sacado_cnpj")+"");


            // inicio uma outrathread para imprimir o boleto
            Thread printerThread = new Thread() {
                // setting the behavior we want from the Thread
                @Override
                public void run() {
                    threadHandlerBoleto.sendEmptyMessage(0);
                }
            };
            printerThread.start();

            btntype = 1;
            Thread currentThread = new Thread(this);
            currentThread.start();

            } else {
                Toast.makeText(this,
                        "Não foi possível realizar a conexão  com a impressora!",
                        Toast.LENGTH_LONG).show();
            }



    }



    public void genBoletoResponse(){


        Response.Listener<String> ResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonResponse = new JSONObject(response);

                    JSONArray jArray        = jsonResponse.getJSONArray("posts");
                    JSONObject e            = jArray.getJSONObject(0);
                    String s                = e.getString("post");
                    JSONObject jObject      = new JSONObject(s);

                    genBoletoByClassA72(jObject);


                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        };

        BoletoResponse boletoResponse = new BoletoResponse(ResponseListener);
        RequestQueue queue = Volley.newRequestQueue(BluetoothPrintService.this);
        queue.add(boletoResponse);

    }

    public static void genReciboByClassA7() {
        connected = receiptprinter.connect(false);
        if (connected) {
           // status = (TextView) findViewById(R.id.status);
            //status.setText(status.getText().toString()
              //      .replace("Desconectado", "Conectado"));
            // btnPrint.setEnabled(false);
            //enabledButtons(false);

            // teste recibo
            long date = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dateString = sdf.format(date);
            // template igual ao padrão ReceiptPrinterA8.template
            final String[] template = new String[]{"------------------------------------------------------","Recebi(emos) o bloqueto de cobrança com as",
                    "caracteristicas descritas neste", "comprovante de entrega.\r\n", "Ciente: __________________________",
                    "         ", "DATA  :_______/_______/_______\r\n", "Data do Documento "+dateString+"",
                    " ", " ", "     ",
                    "                              ",
                    "                ",
                    "                            ",
                    "   "};
            // tags presentes no template sem as duas de produto, pois elas
            // serão usadas internamente pelo método
            final String[] tags = {"#prot", "#forne", "#cnpj", "#cliente",
                    "#2cliente", "#2cnpj", "#end", "#2end", "#cond", "#rec",
                    "#ide", "#data", "#hora"};
            // dados correspondentes as tags
            final String[] data = {"0000", "Moggiana Ltda",
                    "123.456.789/0001-99",
                    "DRISERVE EMPRESA MINERACAO FONTES AGUA",
                    "S MINERAL LTDA EPP", "00000000016063",
                    "AV JACU-PESSEGO, 4710", "VILA JACUI SAO PAULO SP BRASIL",
                    "A VISTA", "JOSE", "TESTE", "13-03-2013", "15:06"};
            // igual ao ReceiptPrinterA8.testDataProduct - contém o nome e
            // quantidade do produto, o nome irá ser dividido em várias linhas
            // se ultrapassar 20 caracteres q é o tamanho do campo nome
            final String[][] testDataProduct = {{"           ", "  "},
                    {"                     ", "  "}};
            // método que recebe o template, as tags e dados correspondentes, o
            // índice da linha que contém as tags de produto e os dados a
            // preencher os produtos
            receiptprinter.genAllByFields(template, tags, data, 11,
                    testDataProduct);

            // inicio uma outrathread para imprimir o boleto
            Thread printerThread = new Thread() {
                // setting the behavior we want from the Thread
                @Override
                public void run() {
                    threadHandlerReceipt.sendEmptyMessage(0);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    threadHandlerReceipt.sendEmptyMessage(1);
                }
            };
            printerThread.start();

        } else {
            /*Toast.makeText(this,
                    "Não foi possível realizar a conexão com a impressora!",
                    Toast.LENGTH_LONG).show();*/
        }
    }

    public void genNotaByClassA7() {
        connected = nfeprinter.connect(false);
        if (connected) {
           /* status = (TextView) findViewById(R.id.status);
            status.setText(status.getText().toString()
                    .replace("Desconectado", "Conectado"));
            enabledButtons(false);*/

            // #Impressão
            // header
            nfeprinter.getMobilePrinter().Reset();
            nfeprinter.genNotaHeaderByFields(NfePrinterA7.templateHeader,
                    NfePrinterA7.tagsHeader, new String[]{
                            "Input Service LTDA", "652", "2"});
            // descrição
            StringBuilder valornfeStr = new StringBuilder();
            valornfeStr
                    .append("         Danfe Simplificado          1-Saida    ");
            valornfeStr
                    .append("         Documento Auxiliar da       NFe 617    ");
            valornfeStr
                    .append("         Nota Fiscal Eletronica      Serie 2    ");
            valornfeStr
                    .append("                                                ");
            try {
                nfeprinter.getMobilePrinter()
                        .SendString(valornfeStr.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // accesskey
            nfeprinter
                    .genAccessKey("1234567890123456789012345678901234567890123");

            // barcode
            nfeprinter
                    .genBarCode128HorNRI6("1234567890123456789012345678901234567890123");

            // natop - é necessário resetar a impressora após a impressão do
            // código de barras, pois é nesta função é colocado o texto em
            // negrito
            nfeprinter.getMobilePrinter().Reset();
            nfeprinter.genAllByFields(NfePrinterA7.templateNatOp1,
                    NfePrinterA7.tagsNatOp1, new String[]{
                            "Venda de Mercadoria, adquirida de", "terceiros"});

            // emitente
            // nfeprinter.getMobilePrinter().Reset();
            nfeprinter.genAllByFields(NfePrinterA7.templateEmitter,
                    NfePrinterA7.tagsEmitter, new String[]{
                            "Input Service LTDA", "Deputado",
                            "Cotia - Sao Paulo", "07654", "09678", "98989",
                            "999999999"});

            // destinatario
            // nfeprinter.getMobilePrinter().Reset();
            nfeprinter.genAllByFields(NfePrinterA7.templateReceiver,
                    NfePrinterA7.tagsReceiver, new String[]{
                            "Distribuidora Imaginaria Ltda", "11-04-2012",
                            "Av Nossa Senhora Aparecida N123",
                            "Sao Paulo - SP", "11-04-2012", "54321-123",
                            "11 9291 7463", "9999999999/0001-99", "18:35",
                            "999 999 999 999"}, 1, NfePrinterA7.sizeslastitem,
                    1);

            // produtos
            // nfeprinter.getMobilePrinter().Reset();
            nfeprinter.genNotaProductsByFields(NfePrinterA7.templateProduct,
                    NfePrinterA7.tagsProduct, NfePrinterA7.sizesProduct,
                    new String[][]{
                            {"impressora portatil a7", "pc", "0010", "999,00",
                                    "980,00"},
                            {"Bobina de papel termico", "pc", "0100",
                                    "  4,00", "500,00"},
                            {"Bobina de papel termico", "pc", "0100",
                                    "  4,00", "500,00"}}, "9999,00");
            // descrição
            nfeprinter.getMobilePrinter().Reset();
            valornfeStr = new StringBuilder();
            valornfeStr.append("\n\n\n\n\n");
            try {
                nfeprinter.getMobilePrinter()
                        .SendString(valornfeStr.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // */

            // foram incluidas estas linhas no if
            btntype = 0;
            Thread currentThread = new Thread(this);
            currentThread.start();
        } else {
            Toast.makeText(this,
                    "Não foi possível realizar a conexão com a impressora!",
                    Toast.LENGTH_LONG).show();
        }
    }

    // manages Threads messages of Boleto
    static private Handler threadHandlerBoleto = new Handler() {
        public void handleMessage(Message msg) {
            // handling messages and acting for the Thread goes here
            boletoprinter.getMobilePrinter().Reset();
            boletoprinter.printBoleto(boleto);
        }
    };

    // manages Threads messages of Receipt
    static private Handler threadHandlerReceipt = new Handler() {
        public void handleMessage(Message msg) {
            // handling messages and acting for the Thread goes here
            if (msg.what == 1) {
                //enabledButtons(true);

            } else {
                while (receiptprinter.getMobilePrinter().QueryPrinterStatus() != 0) {
                }
                receiptprinter.disconnect();
            }
        }
    };

    @Override
    public void run() {
        threadHandler.sendEmptyMessage(0);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadHandler.sendEmptyMessage(1);
    }

    static private Handler threadHandler = new Handler() {


        public void handleMessage(Message msg) {
            //
            if (btntype == 1) {
                if (msg.what == 1) {
                    //enabledButtons(true);
                } else {
                    while (boletoprinter.getMobilePrinter()
                            .QueryPrinterStatus() != 0) {
                    }

                    boletoprinter.disconnect();

                    BluetoothPrintService.genReciboByClassA7();
                    //BluetoothPrintService.
                     jObject = null;
                }
            } else {
                if (msg.what == 1) {
                   // enabledButtons(true);
                } else {
                    while (nfeprinter.getMobilePrinter().QueryPrinterStatus() != 0) {
                    }
                    nfeprinter.disconnect();
                }
            }
            /*status.setText(status.getText().toString()
                    .replace("Conectado", "Desconectado"));*/
        }
    };

    private class BufferedImage {
    }
}