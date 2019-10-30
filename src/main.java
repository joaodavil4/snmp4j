

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import java.io.IOException;
import java.util.*;

public class main {
    private static Configs configs;
    private static Snmp snmp;
    private static Scanner in = new Scanner(System.in);
    private static int snmpVersion  = SnmpConstants.version1;
    private static boolean isFirstTime = true;
    private static int getValue = 0;


    public static void main(String[] args) throws Exception {


        System.out.println("Digite o IP o qual deseja receber informações: ");
        String ipAddress = in.next();

        System.out.println("Digite a comunidade ");
        String community = in.next();

        System.out.println("Digite a Oid ");
        String oidValue = in.next();

        configs = new Configs(ipAddress, community, oidValue, "161");

        int optionSelected;

        while (true) {

            abreMenu();
            System.out.println("Qual operação você deseja fazer:");
            optionSelected = in.nextInt();

            verifyOption(optionSelected);
            java.awt.Toolkit.getDefaultToolkit().beep();
        }
    }


    static void alteraIp(){
        System.out.println("Digite o IP o qual deseja receber informações: ");
        configs.setIpAddress(in.next());
    }

    static void alteraCommunity(){
        System.out.println("Digite a comunidade ");
        configs.setCommunity(in.next());
    }

    static void alteraOid(){
        System.out.println("Digite a OID: ");
        configs.setOidValue(in.next());
    }

    static void alteraPort(){
        System.out.println("Digite a porta: ");
        configs.setPort(in.next());
    }

    static void verifyOption(int optionSelected) throws Exception {
        switch (optionSelected){
            case 1:
                get();
                break;
            case 2:
                getnext();
                break;
            case 3:
                ifSet();
                break;
            case 4:
                ifBulk();
                break;
            case 5:
                ifGetDelta();
                break;
            case 6:
                snmpTable();
                break;
            case 7:
                snmpWalk();
                break;
            case 8:
                abreMenuConfigs();
                verifyOptionsConfigs(in.nextInt());
                break;
            case 9:
                System.exit(0);

        }
    }

    static void verifyOptionsConfigs(int optionSelected) throws Exception {
        switch (optionSelected) {
            case 1:
                alteraIp();
                break;
            case 2:
                alteraCommunity();
                break;
            case 3:
                alteraOid();
                break;
            case 4:
                alteraPort();
                break;
            case 5:
                break;
        }
    }

    static void ifGetDelta(){
        System.out.println("Digite a amostra: ");
        int amostra = in.nextInt();

        System.out.println("Digite o tempo: ");
        int tempo = in.nextInt();

        try {
            runGetDelta(amostra, tempo);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    static void ifSet(){
        System.out.println("Valor a ser alterado: ");
        String valor = in.next();

        try {
            set(valor);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    static void ifBulk(){
        System.out.println("Digite os non repeaters: ");
        int nonRepeater = in.nextInt();

        System.out.println("Digite os max repeaters: ");
        int maxRepeater = in.nextInt();

        try {
            getBulk(nonRepeater, maxRepeater);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    static void abreMenu(){
        System.out.println("1) Get" +
                "\n2)GetNext" +
                "\n3)Set" +
                "\n4)GetBulk" +
                "\n5)GetDelta" +
                "\n6)GetTable" +
                "\n7)Walk" +
                "\n8)Configurações" +
                "\n9)Sair");
    }

    static void abreMenuConfigs(){
        System.out.println("1)Alterar o IP do Agente" +
                "\n2)Alterar a comunidade" +
                "\n3)Alterar o OID" +
                "\n4)Alterar a porta" +
                "\n5)Voltar");
    }

    static void initSnmp() throws IOException {
        String address = configs.getIpAddress() + "/" + configs.getPort();

        // Create TransportMapping and Listen
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        // Create Target Address object
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(configs.getCommunity()));
        comtarget.setVersion(snmpVersion);
        comtarget.setAddress(new UdpAddress(address));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        // Create the PDU object
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(configs.getOidValue())));
        pdu.setType(PDU.GET);
        pdu.setRequestID(new Integer32(1));

        // Create Snmp object for sending data to Agent
        snmp = new Snmp(transport);

    }

    public static void get()throws Exception{
        System.out.println("SNMP GET");

        String address = configs.getIpAddress() + "/" + configs.getPort();

        // Create TransportMapping and Listen
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        // Create Target Address object
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(configs.getCommunity()));
        comtarget.setVersion(snmpVersion);
        comtarget.setAddress(new UdpAddress(address));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        // Create the PDU object
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(configs.getOidValue())));
        pdu.setType(PDU.GET);
        pdu.setRequestID(new Integer32(1));

        // Create Snmp object for sending data to Agent
        Snmp snmp = new Snmp(transport);

        System.out.println("Sending Request to Agent...");
        ResponseEvent response = snmp.get(pdu, comtarget);


        // Process Agent Response
        if (response != null)
        {
            System.out.println("Got Response from Agent");
            PDU responsePDU = response.getResponse();

            if (responsePDU != null)
            {
                int errorStatus = responsePDU.getErrorStatus();
                int errorIndex = responsePDU.getErrorIndex();
                String errorStatusText = responsePDU.getErrorStatusText();

                if (errorStatus == PDU.noError)
                {
                    System.out.println("Snmp Get Response = " + responsePDU.getVariableBindings());
                }
                else
                {
                    System.out.println("Error: Request Failed");
                    System.out.println("Error Status = " + errorStatus);
                    System.out.println("Error Index = " + errorIndex);
                    System.out.println("Error Status Text = " + errorStatusText);
                }
            }
            else
            {
                System.out.println("Error: Response PDU is null");
            }
        }
        else
        {
            System.out.println("Error: Agent Timeout... ");
        }
        snmp.close();
    }

    public static void getDelta()throws Exception{
        if (isFirstTime){
            System.out.println("SNMP GET DELTA");

            String address = configs.getIpAddress() + "/" + configs.getPort();

            // Create TransportMapping and Listen
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();

            // Create Target Address object
            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(new OctetString(configs.getCommunity()));
            comtarget.setVersion(snmpVersion);
            comtarget.setAddress(new UdpAddress(address));
            comtarget.setRetries(2);
            comtarget.setTimeout(1000);

            // Create the PDU object
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(configs.getOidValue())));
            pdu.setType(PDU.GET);
            pdu.setRequestID(new Integer32(1));

            // Create Snmp object for sending data to Agent
            Snmp snmp = new Snmp(transport);

            System.out.println("Sending Request to Agent...");
            ResponseEvent response = snmp.get(pdu, comtarget);

            if (response != null) getValue = Integer.parseInt(response.getResponse().getVariableBindings().elementAt(0).toValueString());
            else System.out.println("Error: Agent Timeout... ");

            snmp.close();
            isFirstTime = false;


        }
        else {
            String address = configs.getIpAddress() + "/" + configs.getPort();

            // Create TransportMapping and Listen
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();

            // Create Target Address object
            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(new OctetString(configs.getCommunity()));
            comtarget.setVersion(snmpVersion);
            comtarget.setAddress(new UdpAddress(address));
            comtarget.setRetries(2);
            comtarget.setTimeout(1000);

            // Create the PDU object
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(configs.getOidValue())));
            pdu.setType(PDU.GET);
            pdu.setRequestID(new Integer32(1));

            // Create Snmp object for sending data to Agent
            Snmp snmp = new Snmp(transport);

            System.out.println("Sending Request to Agent...");
            ResponseEvent response = snmp.get(pdu, comtarget);


            // Process Agent Response
            if (response != null) {
                System.out.println("Got Response from Agent");
                PDU responsePDU = response.getResponse();
                int aux = 0;
                aux = getValue - Integer.parseInt(response.getResponse().getVariableBindings().elementAt(0).toValueString());
                getValue = Integer.parseInt(response.getResponse().getVariableBindings().elementAt(0).toValueString());

                System.out.println(aux);
                if (responsePDU != null) {
                    int errorStatus = responsePDU.getErrorStatus();
                    int errorIndex = responsePDU.getErrorIndex();
                    String errorStatusText = responsePDU.getErrorStatusText();

                    if (errorStatus == PDU.noError) {
                        System.out.println("Snmp Get Response = " + getValue);
                    } else {
                        System.out.println("Error: Request Failed");
                        System.out.println("Error Status = " + errorStatus);
                        System.out.println("Error Index = " + errorIndex);
                        System.out.println("Error Status Text = " + errorStatusText);
                    }
                } else {
                    System.out.println("Error: Response PDU is null");
                }
            } else {
                System.out.println("Error: Agent Timeout... ");
            }

            snmp.close();
        }
    }

    public static void getnext()throws Exception{
        System.out.println("SNMP GET-NEXT");

        // Create TransportMapping and Listen
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        // Create Target Address object
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(configs.getCommunity()));
        comtarget.setVersion(snmpVersion);
        comtarget.setAddress(new UdpAddress(configs.getIpAddress() + "/" + configs.getPort()));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        // Create the PDU object
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(configs.getOidValue())));
        pdu.setRequestID(new Integer32(1));
        pdu.setType(PDU.GETNEXT);


        // Create Snmp object for sending data to Agent
        Snmp snmp = new Snmp(transport);

        System.out.println("Sending GetNext Request to Agent ...");
        ResponseEvent response = snmp.getNext(pdu, comtarget);

        // Process Agent Response
        if (response != null)
        {
            System.out.println("\nResponse:\nGot GetNext Response from Agent...");
            PDU responsePDU = response.getResponse();

            if (responsePDU != null)
            {
                int errorStatus = responsePDU.getErrorStatus();
                int errorIndex = responsePDU.getErrorIndex();
                String errorStatusText = responsePDU.getErrorStatusText();

                if (errorStatus == PDU.noError)
                {
                    System.out.println("Snmp GetNext Response for sysObjectID = " + responsePDU.getVariableBindings());
                }
                else
                {
                    System.out.println("Error: Request Failed");
                    System.out.println("Error Status = " + errorStatus);
                    System.out.println("Error Index = " + errorIndex);
                    System.out.println("Error Status Text = " + errorStatusText);
                }
            }
            else
            {
                System.out.println("Error: GetNextResponse PDU is null");
            }
        }
        else
        {
            System.out.println("Error: Agent Timeout... ");
        }
        snmp.close();

    }

    static void set(String newValue)throws IOException
    {
        System.out.println("SNMP SET");

        String address = configs.getIpAddress() + "/" + configs.getPort();

        // Create TransportMapping and Listen
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        // Create Target Address object
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(configs.getCommunity()));
        comtarget.setVersion(snmpVersion);
        comtarget.setAddress(new UdpAddress(address));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        // Create the PDU object
        PDU pdu = new PDU();

        // Setting the Oid and Value for sysContact variable
        OID oid = new OID(configs.getOidValue());
        Variable var = new OctetString(newValue);
        VariableBinding varBind = new VariableBinding(oid,var);
        pdu.add(varBind);

        pdu.setType(PDU.SET);
        pdu.setRequestID(new Integer32(1));

        // Create Snmp object for sending data to Agent
        Snmp snmp = new Snmp(transport);

        System.out.println("\nRequest:\n[ Note: Set Request is sent for sysContact oid in RFC 1213 MIB.");
        System.out.println("Set operation will change the sysContact value to " + newValue );
        System.out.println("Once this operation is completed, Querying for sysContact will get the value = " + newValue + " ]");

        System.out.println("Request:\nSending Snmp Set Request to Agent...");
        ResponseEvent response = snmp.set(pdu, comtarget);

        // Process Agent Response
        if (response != null)
        {
            System.out.println("\nResponse:\nGot Snmp Set Response from Agent");
            PDU responsePDU = response.getResponse();

            if (responsePDU != null)
            {
                int errorStatus = responsePDU.getErrorStatus();
                int errorIndex = responsePDU.getErrorIndex();
                String errorStatusText = responsePDU.getErrorStatusText();

                if (errorStatus == PDU.noError)
                {
                    System.out.println("Snmp Set Response = " + responsePDU.getVariableBindings());
                }
                else
                {
                    System.out.println("Error: Request Failed");
                    System.out.println("Error Status = " + errorStatus);
                    System.out.println("Error Index = " + errorIndex);
                    System.out.println("Error Status Text = " + errorStatusText);
                }
            }
            else
            {
                System.out.println("Error: Response PDU is null");
            }
        }
        else
        {
            System.out.println("Error: Agent Timeout... ");
        }
        snmp.close();

    }

    public static void setOid(String oid) throws IOException {
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        ScopedPDU pdu = new ScopedPDU();
        OID targetOID = new OID(oid);
        pdu.add(new VariableBinding(targetOID));
        Snmp snmp = new Snmp(transport);
    }

    static void snmpWalk() throws IOException {
        List<PDU> pduList = new ArrayList<>();

        // Create TransportMapping and Listen
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        // Create Target Address object
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(configs.getCommunity()));
        comtarget.setVersion(snmpVersion);
        comtarget.setAddress(new UdpAddress(configs.getIpAddress() + "/" + configs.getPort()));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        // Create the PDU object
        PDU pdu = new PDU();
        OID targetOID = new OID(configs.getOidValue());
        pdu.add(new VariableBinding(targetOID));
        pdu.setRequestID(new Integer32(1));
        pdu.setType(PDU.GETNEXT);

        Snmp snmp = new Snmp(transport);

        boolean finished = false;
        while (!finished) {
            VariableBinding vb = null;

            ResponseEvent respEvent = snmp.getNext(pdu, comtarget);
            PDU response = respEvent.getResponse();

            if (null == response) {
                break;
            } else {
                vb = response.get(0);
            }
            // check finish
            finished = checkWalkFinished(targetOID, pdu, vb);
            if (!finished) {
                pduList.add(response);

                // Set up the variable binding for the next entry.
                pdu.setRequestID(new Integer32(0));
                pdu.set(0, vb);
            }
        }

        for (PDU pd: pduList) {
            System.out.println(pd.toString());
        }
    }

    private static boolean checkWalkFinished(OID targetOID, PDU pdu,
                                             VariableBinding vb) {
        boolean finished = false;
        if (pdu.getErrorStatus() != 0) {
            System.out.println("[true] responsePDU.getErrorStatus() != 0 ");
            System.out.println(pdu.getErrorStatusText());
            finished = true;
        } else if (vb.getOid() == null) {
            System.out.println("[true] vb.getOid() == null");
            finished = true;
        } else if (vb.getOid().size() < targetOID.size()) {
            System.out.println("[true] vb.getOid().size() < targetOID.size()");
            finished = true;
        } else if (targetOID.leftMostCompare(targetOID.size(), vb.getOid()) != 0) {
            System.out.println("[true] targetOID.leftMostCompare() != 0");
            finished = true;
        } else if (Null.isExceptionSyntax(vb.getVariable().getSyntax())) {
            System.out
                    .println("[true] Null.isExceptionSyntax(vb.getVariable().getSyntax())");
            finished = true;
        } else if (vb.getOid().compareTo(targetOID) <= 0) {
            System.out.println("[true] Variable received is not "
                    + "lexicographic successor of requested " + "one:");
            System.out.println(vb.toString() + " <= " + targetOID);
            finished = true;
        }
        return finished;

    }


    static public void snmpTable() throws IOException
    {
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        Snmp snmp = new Snmp(transport);
        TableUtils tUtils = new TableUtils(snmp, new DefaultPDUFactory());

        // Create Target Address object
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(configs.getCommunity()));
        comtarget.setVersion(snmpVersion);
        comtarget.setAddress(new UdpAddress(configs.getIpAddress() + "/" + configs.getPort()));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        List<TableEvent> events = tUtils.getTable(comtarget, new OID[]{new OID(configs.getOidValue())}, null, null);

        List<SNMPTriple> snmpList = new ArrayList<SNMPTriple>();

        for (TableEvent event : events) {
            if(event.isError()) {
                System.out.println("SNMP event error: "+event.getErrorMessage());
                continue;
                //throw new RuntimeException(event.getErrorMessage());
            }
            for(VariableBinding vb: event.getColumns()) {
                String key = vb.getOid().toString();
                String value = vb.getVariable().toString();
                snmpList.add(new SNMPTriple(key, "", value));
            }
        }


        for (SNMPTriple result : snmpList){
            System.out.println(result.name + " - " + result.oid + "-" + result.value);
        }
    }

    public static class SNMPTriple
    {
        public String oid;
        public String name;
        public String value;

        public SNMPTriple(String oid, String name, String value)
        {
            this.oid = oid;
            this.name = name;
            this.value = value;
        }
    }

    static public void getBulk(int nonRepeater, int maxRepeater) throws IOException {
        Snmp snmp = null;

        try {

            // Create TransportMapping and Listen
            TransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            transport.listen();

            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(new OctetString(configs.getCommunity()));
            comtarget.setVersion(snmpVersion);
            comtarget.setAddress(new UdpAddress(configs.getIpAddress() + "/" + configs.getPort()));
            comtarget.setRetries(2);
            comtarget.setTimeout(1000);

            PDU pdu = new PDU();
            pdu.setType(PDU.GETBULK);
            pdu.setMaxRepetitions(maxRepeater);
            pdu.setNonRepeaters(nonRepeater);
            pdu.add(new VariableBinding(new OID(configs.getOidValue())));

            ResponseEvent responseEvent = snmp.send(pdu, comtarget);
            PDU response = responseEvent.getResponse();

            // Process Agent Response
            if (response != null) {
                for(VariableBinding vb : response.getVariableBindings()) {
                    System.out.println(vb.getOid() + " - "+vb.getVariable().toString());
                }
            } else {
                System.err.println("Error: Agent Timeout... ");
            }

        } catch (NullPointerException ignore) {
            // The variable table is null
        } finally {
            if (snmp != null) snmp.close();
        }
    }









    public static void runGetDelta(int amostra, int tempo)throws Exception{


        for(int i=1; i<=amostra; i++) {
            getDelta();
            try {
                Thread.sleep(tempo*1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }



    }
}
