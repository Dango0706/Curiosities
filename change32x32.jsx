var inputFolder = Folder.selectDialog("选择包含SVG的文件夹");
if (inputFolder) {
    // 创建svg子文件夹（如果不存在）
    var svgFolder = new Folder(inputFolder.fsName + "/svg");
    if (!svgFolder.exists) svgFolder.create();

    var files = inputFolder.getFiles(/\.svg$/i);
    for (var i = 0; i < files.length; i++) {
        var svgFile = files[i];

        // 转换SVG到PNG
        var doc = app.open(svgFile);
        doc.resizeImage(32, 32);
        var pngPath = svgFile.path + "/" + svgFile.name.replace(/\.svg$/i, ".png");
        var exportOptions = new ExportOptionsSaveForWeb();
        exportOptions.format = SaveDocumentType.PNG;
        exportOptions.PNG8 = false;
        doc.exportDocument(new File(pngPath), ExportType.SAVEFORWEB, exportOptions);
        doc.close(SaveOptions.DONOTSAVECHANGES);

        // 移动原始SVG到svg子文件夹
        var newSvgPath = svgFolder.fsName + "/" + svgFile.name;
        svgFile.rename(newSvgPath);
    }
    alert("转换完成！共处理 " + files.length + " 个文件，SVG已移至/svg文件夹");
}