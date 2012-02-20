package eu.wisebed.wisedb.controller;

import eu.wisebed.wisedb.model.Capability;
import eu.wisebed.wisedb.model.LastNodeReading;
import eu.wisebed.wisedb.model.Node;
import eu.wisebed.wisedb.model.NodeCapability;
import eu.wisebed.wisedb.model.Setup;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

/**
 * CRUD operations for NodeCapabilities entities.
 */
@SuppressWarnings("unchecked")
public class NodeCapabilityControllerImpl extends AbstractController<NodeCapability> implements NodeCapabilityController {


    /**
     * static instance(ourInstance) initialized as null.
     */
    private static NodeCapabilityController ourInstance = null;

    /**
     * Source literal.
     */
    private static final String NODE = "node";

    /**
     * Capabilities literal.
     */
    private static final String CAPABILITY = "capability";
    /**
     * Id literal.
     */
    private static final String ID = "id";

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(NodeCapabilityControllerImpl.class);

    /**
     * Public constructor .
     */
    public NodeCapabilityControllerImpl() {
        // Does nothing
        super();
    }

    /**
     * NodeCapabilityController is loaded on the first execution of
     * NodeCapabilityController.getInstance() or the first access to
     * NodeCapabilityController.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static NodeCapabilityController getInstance() {
        synchronized (NodeCapabilityControllerImpl.class) {
            if (ourInstance == null) {
                ourInstance = new NodeCapabilityControllerImpl();
            }
        }

        return ourInstance;
    }

    /**
     * Prepares and inserts a capability to the persistence with the provided capability name.
     *
     * @param capabilityName , a capability name.
     * @return returns the inserted capability instance.
     */
    public NodeCapability prepareInsertNodeCapability(final String capabilityName, final String nodeId) {
        LOGGER.info("prepareInsertNodeCapability(" + capabilityName + "," + nodeId + ")");

        Capability capability = CapabilityControllerImpl.getInstance().getByID(capabilityName);
        if (capability == null) {
            capability = CapabilityControllerImpl.getInstance().prepareInsertCapability(capabilityName);
        }

        final Node node = NodeControllerImpl.getInstance().getByID(nodeId);

        final NodeCapability nodeCapability = new NodeCapability();

        nodeCapability.setCapability(capability);
        nodeCapability.setNode(node);

        final LastNodeReading lastNodeReading = new LastNodeReading();
        lastNodeReading.setNodeCapability(nodeCapability);

        nodeCapability.setLastNodeReading(lastNodeReading);

        add(nodeCapability);

//        NodeCapabilityController.getInstance().update(nodeCapability);

        LastNodeReadingControllerImpl.getInstance().add(lastNodeReading);


        return nodeCapability;
    }


    public long count() {
        LOGGER.info("count()");
        final Session session = getSessionFactory().getCurrentSession();
        final Criteria criteria = session.createCriteria(NodeCapability.class);
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.list().get(0);
    }

    public List<Capability> list() {
        LOGGER.info("list()");
        final Session session = getSessionFactory().getCurrentSession();
        final Criteria criteria = session.createCriteria(NodeCapability.class);
        List<Capability> capabilities = new ArrayList<Capability>();
        List list = criteria.list();
        for (Object obj : criteria.list()) {
            if (obj instanceof NodeCapability) {
                final NodeCapability cap = (NodeCapability) obj;
                final Capability capability = new Capability();
                capability.setName(cap.getCapability().getName());
                if (capability != null) {
                    capabilities.add(capability);
                }
            }
        }
        return capabilities;
    }

    public NodeCapability getByID(int id) {
        LOGGER.info("getByID(" + id + ")");
        return super.getByID(new NodeCapability(), id);
//        final Session session = getSessionFactory().getCurrentSession();
//        final Criteria criteria = session.createCriteria(NodeCapability.class);
//        criteria.add(Restrictions.eq(ID, id));
//        Object obj = criteria.list().get(0);
//
//        if (obj instanceof NodeCapability) {
//
//            return (NodeCapability) obj;
//        }
//
//        return null;
    }

    public NodeCapability getByID(final Node node, final String capabilityName) {
        final Capability capability = CapabilityControllerImpl.getInstance().getByID(capabilityName);
        LOGGER.debug("getByID(" + node.getId() + "," + capabilityName + ")");
        final Session session = getSessionFactory().getCurrentSession();
        final Criteria criteria = session.createCriteria(NodeCapability.class);
        criteria.add(Restrictions.eq(NODE, node));
        criteria.add(Restrictions.eq(CAPABILITY, capability));
        return (NodeCapability) criteria.uniqueResult();
    }

    public NodeCapability getByID(final Node node, final Capability capability) {
        LOGGER.debug("getByID(" + node.getId() + "," + capability + ")");
        final Session session = getSessionFactory().getCurrentSession();
        final Criteria criteria = session.createCriteria(NodeCapability.class);
        criteria.add(Restrictions.eq(NODE, node));
        criteria.add(Restrictions.eq(CAPABILITY, capability));
        return (NodeCapability) criteria.uniqueResult();
    }

    public boolean isAssociated(final Node node, final Capability capability) {
        LOGGER.debug("isAssociated(" + node.getId() + "," + capability + ")");
        final Session session = getSessionFactory().getCurrentSession();
        final Criteria criteria = session.createCriteria(NodeCapability.class);
        criteria.add(Restrictions.eq(NODE, node));
        criteria.add(Restrictions.eq(CAPABILITY, capability));
        criteria.setProjection(Projections.rowCount());
        if ((Long) criteria.uniqueResult() > 0) {
            return true;
        }
        return false;

    }


    public List<NodeCapability> list(final Node node) {
        LOGGER.debug("list(" + node.getId() + ")");
        final Session session = getSessionFactory().getCurrentSession();
        final Criteria criteria = session.createCriteria(NodeCapability.class);
        criteria.add(Restrictions.eq(NODE, node));
        List<NodeCapability> capabilities = new ArrayList<NodeCapability>();
        List list = criteria.list();
        for (Object obj : criteria.list()) {
            if (obj instanceof NodeCapability) {
                capabilities.add((NodeCapability) obj);
            }
        }
        return capabilities;
    }

    public List<NodeCapability> list(final Setup setup) {
        LOGGER.debug("list(" + setup + ")");
        final List<Node> nodes = NodeControllerImpl.getInstance().list(setup);
        final List<NodeCapability> capabilities = new ArrayList<NodeCapability>();
        if (nodes.size() > 0) {
            final Session session = getSessionFactory().getCurrentSession();
            final Criteria criteria = session.createCriteria(NodeCapability.class);
            criteria.add(Restrictions.in(NODE, nodes));
            List list = criteria.list();
            for (Object obj : criteria.list()) {
                if (obj instanceof NodeCapability) {
                    capabilities.add((NodeCapability) obj);
                }
            }
        }
        return capabilities;
    }

    public List<NodeCapability> list(final Setup setup, final Capability capability) {
        LOGGER.debug("list(" + setup + "," + capability + ")");
        final List<Node> nodes = NodeControllerImpl.getInstance().list(setup);
        final List<NodeCapability> capabilities = new ArrayList<NodeCapability>();

        final Session session = getSessionFactory().getCurrentSession();
        final Criteria criteria = session.createCriteria(NodeCapability.class);
        criteria.add(Restrictions.eq(NODE, nodes));
        criteria.add(Restrictions.eq(CAPABILITY, capability));
        List list = criteria.list();
        for (Object obj : criteria.list()) {
            if (obj instanceof NodeCapability) {
                capabilities.add((NodeCapability) obj);
            }
        }

        return capabilities;
    }


}