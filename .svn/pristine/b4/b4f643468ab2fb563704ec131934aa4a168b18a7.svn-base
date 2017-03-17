package com.fable.hamal.snmpserver.agent;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.TransportMapping;
import org.snmp4j.agent.BaseAgent;
import org.snmp4j.agent.CommandProcessor;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.io.ImportModes;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.mo.ext.AgentppSimulationMib;
import org.snmp4j.agent.mo.snmp.RowStatus;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.StorageType;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.agent.mo.snmp4j.example.Snmp4jHeartbeatMib;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.nonstandard.PrivAES256With3DESKeyExtension;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.transport.TransportMappings;
import org.snmp4j.util.ThreadPool;

import com.fable.hamal.snmpserver.walk.SnmpUtil;

/**
 * 
 * @author yi hong wei
 * @version 1.0
 */
public class SNMPAgent extends BaseAgent {
	private static final String FULLREADVIEW = "1.3.6";
	private static final String FULLWRITEVIEW = "1.3.6.1.4.1.42882.2";
	private static final String FULLNOTIFYVIEW = "1.3.6.1.4.1.42882.2";

	private static final String COMMUNITY_NAME = "public";
	private static final String ADDRESS = "0.0.0.0/161";
	private static final long SLEPPTIME = 1000;

	private static final Logger LOGGER = LoggerFactory.getLogger(SNMPAgent.class);

	protected String address;

	/**
	 * Creates the test agent with a file to read and store the boot counter and
	 * a file to read and store its configuration.
	 * 
	 * @param bootCounterFile
	 *            a file containing the boot counter in serialized form (as
	 *            expected by BaseAgent).
	 * @param configFile
	 *            a configuration file with serialized management information.
	 * @throws IOException
	 *             if the boot counter or config file cannot be read properly.
	 */
	public SNMPAgent(File bootCounterFile, File configFile) throws IOException {
		super(bootCounterFile, configFile, new CommandProcessor(new OctetString(MPv3.createLocalEngineID())));
		agent.setWorkerPool(ThreadPool.create("RequestPool", 4));
		SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES256With3DESKeyExtension());
	}

	protected void registerManagedObjects() {
		MOScalar<OctetString> tasks = new MOScalar<OctetString>(new OID(SnmpUtil.OID_TASKS),
				MOAccessImpl.ACCESS_READ_WRITE, new OctetString(""));
		MOScalar<OctetString> rate = new MOScalar<OctetString>(new OID(SnmpUtil.OID_RATE),
				MOAccessImpl.ACCESS_READ_WRITE, new OctetString(""));
		MOScalar<OctetString> totalTasks = new MOScalar<OctetString>(new OID(SnmpUtil.OID_TOTAL_TASKS),
				MOAccessImpl.ACCESS_READ_WRITE, new OctetString(""));
		MOScalar<OctetString> totalRate = new MOScalar<OctetString>(new OID(SnmpUtil.OID_TOTAL_RATE),
				MOAccessImpl.ACCESS_READ_WRITE, new OctetString(""));
		MOScalar<OctetString> lastTaskTime = new MOScalar<OctetString>(new OID(SnmpUtil.OID_LAST_TASK_TIME),
				MOAccessImpl.ACCESS_READ_WRITE, new OctetString(""));
		try {
			server.register(tasks, null);
			LOGGER.info("Registered MO org.snmp4j.agent.mo.MOScalar oid= " + SnmpUtil.OID_TASKS);
			server.register(totalTasks, null);
			LOGGER.info("Registered MO org.snmp4j.agent.mo.MOScalar oid= " + SnmpUtil.OID_TOTAL_TASKS);
			server.register(rate, null);
			LOGGER.info("Registered MO org.snmp4j.agent.mo.MOScalar oid= " + SnmpUtil.OID_RATE);
			server.register(totalRate, null);
			LOGGER.info("Registered MO org.snmp4j.agent.mo.MOScalar oid= " + SnmpUtil.OID_TOTAL_RATE);
			server.register(lastTaskTime, null);
			LOGGER.info("Registered MO org.snmp4j.agent.mo.MOScalar oid= " + SnmpUtil.OID_LAST_TASK_TIME);
		} catch (DuplicateRegistrationException e) {
			LOGGER.error("Register error", e);
		}
	}

	protected void addNotificationTargets(SnmpTargetMIB targetMIB, SnmpNotificationMIB notificationMIB) {
	}

	protected void addViews(VacmMIB vacm) {
		vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv1, new OctetString("cpublic"), new OctetString("v1v2group"),
				StorageType.nonVolatile);
		vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv2c, new OctetString("cpublic"), new OctetString("v1v2group"),
				StorageType.nonVolatile);

		vacm.addAccess(new OctetString("v1v2group"), new OctetString(COMMUNITY_NAME), SecurityModel.SECURITY_MODEL_ANY,
				SecurityLevel.NOAUTH_NOPRIV, MutableVACM.VACM_MATCH_EXACT, new OctetString("fullReadView"),
				new OctetString("fullWriteView"), new OctetString("fullNotifyView"), StorageType.nonVolatile);

		vacm.addViewTreeFamily(new OctetString("fullReadView"), new OID(FULLREADVIEW), new OctetString(),
				VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
		vacm.addViewTreeFamily(new OctetString("fullWriteView"), new OID(FULLWRITEVIEW), new OctetString(),
				VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
		vacm.addViewTreeFamily(new OctetString("fullNotifyView"), new OID(FULLNOTIFYVIEW), new OctetString(),
				VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
	}

	protected void addUsmUser(USM usm) {
	}

	protected void initTransportMappings() throws IOException {
		transportMappings = new TransportMapping[1];
		Address addr = GenericAddress.parse(address);
		TransportMapping<?> tm = TransportMappings.getInstance().createTransportMapping(addr);
		transportMappings[0] = tm;
	}

	public static void main(String[] args) {
		String address = ADDRESS;
		try {
			SNMPAgent testAgent = new SNMPAgent(new File("SNMP4JTestAgentBC.cfg"),
					new File("SNMP4JTestAgentConfig.cfg"));
			testAgent.address = address;
			LOGGER.info("Initialized Salt");
			testAgent.init();
			testAgent.loadConfig(ImportModes.REPLACE_CREATE);
			testAgent.addShutdownHook();
			testAgent.getServer().addContext(new OctetString(COMMUNITY_NAME));
			testAgent.finishInit();
			testAgent.run();

			while (true) {
				try {
					Thread.sleep(SLEPPTIME);
				} catch (InterruptedException ex1) {
					break;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	protected void unregisterManagedObjects() {
	}

	@SuppressWarnings("unchecked")
	protected void addCommunities(SnmpCommunityMIB communityMIB) {
		Variable[] com2sec = new Variable[] {
				// community name
				new OctetString(COMMUNITY_NAME),
				// security name
				new OctetString("cpublic"),
				// local engine ID
				getAgent().getContextEngineID(),
				// default context name
				new OctetString(COMMUNITY_NAME),
				// transport tag
				new OctetString(),
				// storage type
				new Integer32(StorageType.nonVolatile),
				// row status
				new Integer32(RowStatus.active) };
		MOTableRow<?> row = communityMIB.getSnmpCommunityEntry().createRow(
				new OctetString("public2public").toSubIndex(true), com2sec);
		communityMIB.getSnmpCommunityEntry().addRow(row);
		snmpCommunityMIB.setSourceAddressFiltering(true);
	}

	protected void registerSnmpMIBs() {
		new Snmp4jHeartbeatMib(super.getNotificationOriginator(), new OctetString(), super.snmpv2MIB.getSysUpTime());
		new AgentppSimulationMib();
		super.registerSnmpMIBs();
	}
}
