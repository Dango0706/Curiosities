# JEI集成指南

## 配置JEI依赖

已在`build.gradle`中添加了JEI依赖，版本为`15.20.0.106`，适用于Minecraft 1.20.1。

```gradle
// JEI存储库
maven {
    name = "Progwml6's maven"
    url = "https://dvs1.progwml6.com/files/maven/"
}
maven {
    name = "Jared's maven"
    url = "https://maven.blamejared.com/"
}
maven {
    name = "ModMaven"
    url = "https://modmaven.dev"
}

// JEI依赖
compileOnly fg.deobf("mezz.jei:jei-${minecraft_version}-forge-api:15.20.0.106")
runtimeOnly fg.deobf("mezz.jei:jei-${minecraft_version}-forge:15.20.0.106")
```

## 重新构建项目

添加依赖后，需要刷新Gradle项目并重新构建：

```
gradlew --refresh-dependencies
gradlew build
```

如果使用IDE（如IntelliJ IDEA或Eclipse），请刷新Gradle项目。

## JEI插件类

已创建`CuriositiesJEIPlugin`类作为JEI集成的主要入口点。该类使用`@JeiPlugin`注解，并实现了`IModPlugin`接口。

- `getPluginUid()`: 返回插件的唯一标识符
- `registerCategories()`: 注册自定义配方类别
- `registerRecipes()`: 注册模组的自定义配方
- `registerRecipeCatalysts()`: 注册配方催化剂，将物品与配方类别关联起来

## 示例用法

要为自定义物品添加JEI信息或自定义配方，可以扩展`CuriositiesJEIPlugin`类：

1. 创建自定义配方类别
2. 注册配方
3. 将物品与配方类别关联

详细说明请参考[JEI官方Wiki](https://github.com/mezz/JustEnoughItems/wiki)。 