package mysql.storage.main;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateFactory {

    public SessionFactory getSessionFactory() {
        // Read setting information from an XML file using the standard resource location
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        // Create a metadata sources using the specified service registry
        MetadataSources sources = new MetadataSources(registry);
        // Represents the ORM model as determined from all provided mapping sources
        Metadata metadata = sources.getMetadataBuilder().build();
        // Create SessionFactory object
        return metadata.getSessionFactoryBuilder().build();
    }
}
