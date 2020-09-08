var popupWindow = window.open(
    chrome.extension.getURL("new_popup.html"),
    "popup",
    "width=400,height=400"
);
window.close();