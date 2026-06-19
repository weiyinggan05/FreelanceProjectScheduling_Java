package datasource;

import model.*;
import java.util.*;

//Load jobs from different sources
public interface DataSource {
    String getDescription();
    List<Job> loadJobs() throws Exception;
}
