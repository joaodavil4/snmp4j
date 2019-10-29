

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class main {
    private static String  ipAddress;
    private static String  port = "161";
    private static String  oidValue;
    private static String  community;
    private static Scanner in = new Scanner(System.in);
    private static int    snmpVersion  = SnmpConstants.version1;

    public static void main(String[] args) throws Exception {

        System.out.println("Digite o IP o qual deseja receber informações: ");
        ipAddress = in.nextLine();

        int optionSelected;

        while (true) {
            System.out.println("Digite a comunidade ");
            community = in.nextLine();

            System.out.println("Digite a Oid ");
            oidValue = in.nextLine();

            System.out.println("Qual operação você deseja fazer:");
            abreMenu();
            optionSelected = in.nextInt();

            verifyOption(optionSelected);
            java.awt.Toolkit.getDefaultToolkit().beep();
        }
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
                System.exit(0);

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
        String valor = in.nextLine();

        try {
            setUpTarget(valor);
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
            doGetBulk(nonRepeater, maxRepeater);
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
                "\n8)Sair");
    }

    public static void get()throws Exception{
        System.out.println("SNMP GET");

        String address = ipAddress + "/" + port;

        // Create TransportMapping and Listen
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        // Create Target Address object
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(community));
        comtarget.setVersion(snmpVersion);
        comtarget.setAddress(new UdpAddress(address));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        // Create the PDU object
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oidValue)));
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

    public static void getnext()throws Exception{
        System.out.println("SNMP GET-NEXT Simple Request");

        // Create TransportMapping and Listen
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        // Create Target Address object
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(community));
        comtarget.setVersion(snmpVersion);
        comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        // Create the PDU object
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oidValue)));
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

    static void setUpTarget(/* final String communityName, final String targetIP, string objeto, string tipo*/String valor )throws IOException
    {
        /*
        final InetAddress inetAddress = InetAddress.getByName(ipAddress);
        final Address address = new UdpAddress( inetAddress, portNumber );
        final OctetString community = new OctetString( communityName );
        final TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp( transport );
        snmp.listen();

        // Creating the communityTarget object and setting its properties
        final CommunityTarget communityTarget = new CommunityTarget();
        communityTarget.setCommunity( community );
        // TODO Needs to check also for v2 messages
        communityTarget.setVersion( SnmpConstants.version1 );
        communityTarget.setAddress( address );
        // TODO Need to confirm, whether this value needs to be configures
        communityTarget.setRetries( SnmpManager.DEFAULT_RETRIES );
        // TODO Need to confirm, whether this value needs to be configures
        communityTarget.setTimeout( SnmpManager.DEFAULT_TIMEOUT );
        return communityTarget;*/
    }

    public static void setOid(String oid) throws IOException {
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        ScopedPDU pdu = new ScopedPDU();
        OID targetOID = new OID(oid);
        pdu.add(new VariableBinding(targetOID));
        Snmp snmp = new Snmp(transport);
    }

    public static void snmpWalk() throws IOException {
        List<PDU> pduList = new ArrayList<>();





        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setVersion(snmpVersion);
        target.setAddress(new UdpAddress(ipAddress));
        target.setRetries(2);
        target.setTimeout(1000);

        boolean finished = false;
        while (!finished) {
            VariableBinding vb = null;

            ResponseEvent respEvent = snmp.getNext(pdu, target);

            PDU response = respEvent.getResponse();

            if (null == response) {
                break;
            } else {
                vb = response.get(0);
            }
            // check finish
            //finished = checkWalkFinished(targetOID, pdu, vb);
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


    static public void snmpTable() throws IOException
    {
/*        if(oid == null || oid.isEmpty())return null;
        if(!oid.startsWith("."))oid = "."+oid;
        TableUtils tUtils = new TableUtils(snmp, new DefaultPDUFactory());
        List<TableEvent> events = tUtils.getTable(getTarget(), new OID[]{new OID(oid)}, null, null);

        List<SNMPTriple> snmpList = new ArrayList<SNMPTriple>();

        for (TableEvent event : events) {
            if(event.isError()) {
                logger.warning("SNMP event error: "+event.getErrorMessage());
                continue;
                //throw new RuntimeException(event.getErrorMessage());
            }
            for(VariableBinding vb: event.getColumns()) {
                String key = vb.getOid().toString();
                String value = vb.getVariable().toString();
                snmpList.add(new SNMPTriple(key, "", value));
            }
        }
        return snmpList;*/
    }

    static public void doGetBulk(int nonRepeater, int maxRepeater)
            throws IOException {

/*        Map<String, String> result = new HashMap<>();
        Snmp snmp = null;

        try {

            // Create TransportMapping and Listen
            TransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            transport.listen();

            PDU pdu = new PDU();
            pdu.setType(PDU.GETBULK);
            pdu.setMaxRepetitions(200);
            pdu.setNonRepeaters(0);
            pdu.addAll(vbs);

            ResponseEvent responseEvent = snmp.send(pdu, this.target);
            PDU response = responseEvent.getResponse();

            // Process Agent Response
            if (response != null) {
                for(VariableBinding vb : response.getVariableBindings()) {
                    result.put("." + vb.getOid().toString(), vb.getVariable().toString());
                }
            } else {
                LOG.error("Error: Agent Timeout... ");
            }

        } catch (NullPointerException ignore) {
            // The variable table is null
        } finally {
            if (snmp != null) snmp.close();
        }
        return result;*/
    }

   // @param int n numero amostras
	 // @param int m intervalo tempo
	  //@param OID

    public static void getDelta()throws Exception {
        get();
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
