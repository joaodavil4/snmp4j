

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.util.Scanner;

public class main {
    private static String  ipAddress  = "127.0.0.1"; //perguntar ao usuario

    private static String  port = "161";

    // OID of MIB RFC 1213; Scalar Object = .iso.org.dod.internet.mgmt.mib-2.system.sysDescr.0
    private static String  oidValue  = ".1.3.6.1.2.1.1.1.0";  // //perguntar ao usuario
    // ends with 0 for scalar object

    private static int    snmpVersion  = SnmpConstants.version1;

    private static String  community  = "public";//perguntar ao usuario

    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);

        System.out.println("Digite a Oid ");
        oidValue = in.nextLine();

        System.out.println("Digite a comunidade ");
        community = in.nextLine();

        System.out.println("Digite o IP: ");
        ipAddress = in.nextLine();

        abreMenu();
        int optionSelected;
        optionSelected = in.nextInt();

        switch (optionSelected){
            case 1:
                get(ipAddress, community, oidValue);
            case 2:
                getnext();

        }
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

    static void abreMenu(){
        System.out.println("1) Get\n2) GetNext");
    }

    public static void get(String ip, String community, String oid)throws Exception{
        System.out.println("SNMP GET");

        String address = ip + "/" + port;

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

/*    private Target setUpTarget( final String communityName, final String targetIP, string objeto, string tipo )
            throws IOException
    {
        final InetAddress inetAddress = InetAddress.getByName( targetIP );
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
        return communityTarget;
    }

    public static List<PDU> snmpWalk(Snmp snmp, Target target, String oid) throws IOException {
        List<PDU> pduList = new ArrayList<>();

        ScopedPDU pdu = new ScopedPDU();
        OID targetOID = new OID(oid);
        pdu.add(new VariableBinding(targetOID));

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
            finished = checkWalkFinished(targetOID, pdu, vb);
            if (!finished) {
                pduList.add(response);

                // Set up the variable binding for the next entry.
                pdu.setRequestID(new Integer32(0));
                pdu.set(0, vb);
            }
        }

        return pduList;
    }
    public List<SNMPTriple> querySingleSNMPTableByOID(String oid) throws IOException
    {
        if(oid == null || oid.isEmpty())return null;
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
        return snmpList;
    }*/
}
