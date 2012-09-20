var createDialogsForSequenceDiagramMessages = function () {
    var messagePayloadDialogs = {};
    $(".sequence_diagram_clickable").each(function () {
        var sequenceDiagramMessage = $(this);
        var myScenario = sequenceDiagramMessage.parents(".scenario");
        var scenarioUid = myScenario.attr("id");
        var sequenceDiagramMessageId = sequenceDiagramMessage.attr("sequence_diagram_message_id");

        var dialogContent = myScenario.find('H3[logKey="' + sequenceDiagramMessageId + '"]').next("div").clone();

        var capturedInputAndOutputsName = sequenceDiagramMessage.text();
        sequenceDiagramMessageId = "" + sequenceDiagramMessageId + "_" + scenarioUid;
        messagePayloadDialogs[sequenceDiagramMessageId] = dialogContent.dialog({title:capturedInputAndOutputsName, minWidth:800, stack:false, closeOnEscape:true, autoOpen:false });
    });
    return messagePayloadDialogs;
};

var dialogsCreated = false;
var messagePayloadDialogs = {};
$(document).ready(function () {

    $(".SvgSequenceDiagram").toggleClass("hide");

    $(".sequence_diagram_clickable").click(function () {
        openDialog($(this), "click");
    });

    $(".sequence_diagram_clickable").hover(function () {
        openDialog($(this), "hover");
    }, function () {
        closeHoveredDialog($(this));
    })
});

function dialogForSequenceDiagramMessage(sequenceDiagramMessage) {
    var myScenario = sequenceDiagramMessage.parents(".scenario");
    var scenarioUid = myScenario.attr("id");
    var sequenceDiagramMessageId = "" + sequenceDiagramMessage.attr("sequence_diagram_message_id") + "_" + scenarioUid;
    return messagePayloadDialogs[sequenceDiagramMessageId];
}

function closeHoveredDialog(sequenceDiagramMessage) {
    var dialog = dialogForSequenceDiagramMessage(sequenceDiagramMessage);
    if (dialog.openMethod == "hover") {
        dialog.dialog("close")
    }
}

function openDialog(sequenceDiagramMessage, openMethod) {
    if (!dialogsCreated) {
        messagePayloadDialogs = createDialogsForSequenceDiagramMessages();
        dialogsCreated = true;
    }

    var dialog = dialogForSequenceDiagramMessage(sequenceDiagramMessage);
    dialog.openMethod = openMethod;
    dialog.dialog("open");
}