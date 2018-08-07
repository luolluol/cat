package com.dianping.cat.consumer.build;

import org.unidal.dal.jdbc.configuration.AbstractJdbcResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import java.util.ArrayList;
import java.util.List;

public final class ForwardDatabaseConfigurator extends AbstractJdbcResourceConfigurator {
   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      defineSimpleTableProviderComponents(all, "nuwa", com.dianping.cat.consumer.forward._INDEX.getEntityClasses());
      defineDaoComponents(all, com.dianping.cat.consumer.forward._INDEX.getDaoClasses());

      return all;
   }
}
