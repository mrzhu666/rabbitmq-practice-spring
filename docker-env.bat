::重构容器并运行。方便调试测试容器
set file=docker-compose.env.yml
set service=rabbitmq


::重构容器
docker-compose -f %file% build %service%
::启动容器
docker-compose -f %file% up -d %service%
::查看日志
::start开启新窗口，因为无法ctrl c中断
::cmd /K 防止运行错误自动关闭窗口
start cmd /K docker-compose -f %file% logs %service% -f

