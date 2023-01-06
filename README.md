为了简化配置，settings.xml 被放置在了 .mvn 文件夹下，作为工程配置的一部分，同时，需要修改 mvnw 和 mvnw.cmd 两个文件，增加 -s
.mvn/settings.xml 启动参数，否则单独执行插件或子模块任务时会提示找不到 settings.xml 文件