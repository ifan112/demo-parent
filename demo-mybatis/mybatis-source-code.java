// SqlSessionFactory是用于创建SqlSession的工厂类
// 它解析mybatis配置文件和mapper配置文件，以用于自身实例

SqlSessionFactory sqlSessionFactory = new SqlSessionFactory().build(in) {
    // environment是在创建SqlSessionFactory时，开发者指定的环境标识
    // 在mybatis配置文件中，可以同时声明多个environment。在不同的environment下，可以声明不同的事务管理器和数据源
    // properties是在创建SqlSessionFactory时，开发者自定义的配置属性。它将会与开发者在配置文件中声明的properties合并
    build(in, String environment = null , Properties properties = null) {
        try {
            // xml配置文件解析器
            XMLConfigBuilder configBuilder = new XMLConfigBuilder(in, environment, properties);

            // 开始解析mybatis配置文件
            configBuilder.parse() {
                if (parsed) {
                    // 配置已经解析过了，不可以再次解析。这里抛出异常
                }
                parsed = true;

                // 从根节点/configuration开始解析
                parseConfiguration(XNode root = parser.evalNode("/configuration")) {
                    try {
                        // 解析properties节点
                        propertiesElement(XNode context = root.evalNode("properties"));

                        // 解析settings节点
                        Properties settings = settingsAsProperties(root.evalNode(settings));
                        loadCustomVfs(settings);

                        // 解析typeAliases节点
                        typeAliasElement(root.evalNode("typeAliases"));
                        // 解析plugins节点，注册开发者自定义插件
                        pluginsElement(root.evalNode("plugins"));

                        objectFactoryElement(root.evalNode("objectFactory"));
                        objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
                        reflectorFactoryElement("reflectorFactory");

                        // 应用开发者在配置文件中指定的有关mybatis运行时配置
                        settingsElement(settings);

                        // 解析environments节点，注册开发者自定义的多环境配置
                        environmentsElement(root.evalNode("environments"));
                        databaseIdProviderElement(root.evalNode("databaseIdProvider"));

                        // 解析typeHandler解析，注册开发者自定义的类型处理器
                        typeHandlerElement(root.evalNode("typeHandler"));
                        // 解析mappers节点，扫描mapper文件，注册开发者自定义的Mapper接口
                        mapperElement(root.evalNode("mappers"));
                    } catch (Exception e) {
                        // 解析配置文件过程中发生异常，抛出
                    }
                };
                
                // 配置文件解析完成，返回持有mybatis所有配置的Configuration实例
                return configuration;
            };

            // 返回持有mybatis所有配置的默认SqlSession工厂类对象
            return build(config) {
                return new DefaultSqlSessionFactory(config);
            }
        }
    }
};

  
/* ------------------------------------------------------------------------------------------------------------------ */

// XMLConfigBuilder解析mybatis配置文件，创建Congiuration对象
XMLConfigBuilder
    -- Configuration configuration  // 配置
    -- String environment           // 当前环境标识
    -- boolean parsed               // 是否已经解析过配置文件的标识。mybatis配置文件只可以解析一次，重复解析将会抛出异常
    -- XPathParser parser           // xml配置文件的解析器

// 持有mybatis所有配置
Configuration
    -- Environment environment  // 当前配合下启用的环境
        -- String id                             // 环境标识
        -- TransactionFactory transactionFactory // 事务工厂
        -- DataSource dataSource                 // 数据库连接池
    -- Properties props         // 开发者通过编码和配置文件自定义的所有属性
    -- TypeAliasRegistry typeAliasRegistry      // 类型别名映射。从类型别名映射到数据类型类，例如string->java.lang.String
    -- TypeHandlerRegistry typeHandlerRegistry  // 类型处理器映射。从数据类型类映射到该数据类型的处理器，例如String->StringTypeHandler
  