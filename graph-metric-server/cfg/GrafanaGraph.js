let result = data.series[0].fields[0].values.buffer[0];
let nodes = result.nodes;
let links = result.relationships;
let categories = result.categories;
let categoryGroups = result.categoryGroups;
let categoryGroupsOther = result.categoryGroupsOther;
let params = result.params;

let nodesOther = [];

if (categoryGroupsOther.length != 0 && categoryGroups.length != 0) {
    for (let j = 0; j < nodes.length; j++) {
        if (categoryGroups.length - 1 === nodes[j].groupCategory) {
            nodesOther.push(nodes[j]);
            nodes[j].categoryOther = true;
        }
    }
}

let HORIZONTAL_LAYOUT = params.extraParams.layout === "horizontal";
let USE_LINK_EFFECT = params.extraParams.linkEffect === "true";
let LESS_LINES = params.extraParams.lessLines === "true";

let NODE_PROPS = params.extraParams.nodeProps;
let NODE_FIELD = params.extraParams.nodeField;
let NODE_LABEL = params.extraParams.nodeLabel;

let LINK_PROPS = params.extraParams.linkProps;
let LINK_FIELD = params.extraParams.linkField;
let LINK_LABEL = params.extraParams.linkLabel;

let KEY_FORMATTER = params.extraParams.keyFormatter ? JSON.parse(params.extraParams.keyFormatter) : {};
let VAL_FORMATTER = params.extraParams.valueFormatter ? JSON.parse(params.extraParams.valueFormatter) : {};

let RATE_HEIGHT_TO_WIDTH = 2;
let RATE_DIFF_HEIGHT_TO_WIDTH = 1;
let RATE_USED = nodesOther.length > 0 ? 0.8 : 0.9;
let PADDING_RATE = nodesOther.length > 0 ? 0.1 : 0;
let Y_DIFF = 10;
let X_DIFF = Y_DIFF * RATE_DIFF_HEIGHT_TO_WIDTH;
let HEIGHT_MAX = 1100;
let WIDTH_MAX = HEIGHT_MAX * RATE_HEIGHT_TO_WIDTH;
let HEIGHT_USED = HORIZONTAL_LAYOUT ? HEIGHT_MAX * RATE_USED : HEIGHT_MAX;
let WIDTH_USED = !HORIZONTAL_LAYOUT ? WIDTH_MAX * RATE_USED : WIDTH_MAX;
let AREA_COUNT = categoryGroups.length - categoryGroupsOther.length;
let AREA_PADDING_LEFT = HORIZONTAL_LAYOUT ? 0 : (WIDTH_MAX * 0.1) / 2;
let AREA_WIDTH_REST = HORIZONTAL_LAYOUT ? WIDTH_USED / AREA_COUNT : WIDTH_USED;
let AREA_PADDING_BOTTOM = !HORIZONTAL_LAYOUT ? 0 : (HEIGHT_MAX * 0.1) / 2;
let AREA_HEIGHT_REST = !HORIZONTAL_LAYOUT ? HEIGHT_USED / AREA_COUNT : HEIGHT_USED;

let AREA_REST_PADDING_RATE = 0.5;
let AREA_REST_PADDING_RATE_WIDTH_MUTI_COL = 0.125;

let LINK_EFFECT_SIZE = 5;
let LINK_SIZE_FACTOR = params.extraParams.linkSizeFactor ? params.extraParams.linkSizeFactor : 40;
let MIN_NODE_SIZE = 20;
let STANDARD_NODE_SIZE = 60;
let MUTI_COL_THRESHOLD = (HORIZONTAL_LAYOUT ? HEIGHT_USED * 0.8 / STANDARD_NODE_SIZE : WIDTH_USED * 0.8 / STANDARD_NODE_SIZE) * 2;

let COLOR_SET = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc'];

let nodeMap = {};
let rippleNodes = [];
let rippleNodesConfig = [];
let linksData = [];
let inLinksData = {};
let outLinksData = {};
let flipIndex = 0;
let flipFlag = 1;
let flipIndexOther = 0;
let categoryGroupInArr = [];

function paddingChar(oStr, character, length, rtl) {
    let tStr = "";
    if (oStr.length > length) {
        tStr = oStr.substr(0, length);
    } else {
        for (let i = 0; i < length - oStr.length; i++) {
            tStr += character;
        }
        if (rtl) {
            tStr = oStr + tStr;
        } else {
            tStr = tStr + oStr;
        }
    }
    return tStr;
}

function getRandomFloat(min, max) {
    return Math.random() * (max - min) + min;
}

function getNodeSize(categoryCount) {
    return Math.max(MIN_NODE_SIZE,
        Math.min(STANDARD_NODE_SIZE, (HORIZONTAL_LAYOUT ? AREA_HEIGHT_REST : AREA_WIDTH_REST) / categoryCount * 2 / 3));
}

for (let i = 0; i < nodes.length; i++) {
    //用于设置tip下标，用户唤醒tooltip
    nodes[i].tipIndex = i;

    //以id为key放入map中。
    nodeMap[nodes[i].id] = nodes[i];

    //记录每类节点的总数。
    if (!categoryGroups[nodes[i].groupCategory].count) {
        categoryGroups[nodes[i].groupCategory].count = 0;
    }
    categoryGroups[nodes[i].groupCategory].count++;

    //记录每个节点在该类别中的序号。
    nodes[i].categoryIndex = categoryGroups[nodes[i].groupCategory].count;
}

let AREA_COUNT_ADAPT = 0;
for (let i = 0; i < AREA_COUNT; i++) {
    let col = Math.floor(categoryGroups[i].count / MUTI_COL_THRESHOLD + 1);
    col = Math.min(col, 2);
    AREA_COUNT_ADAPT += col;
}

let AREA_WIDTH_REST_ADAPT = HORIZONTAL_LAYOUT ? WIDTH_USED / AREA_COUNT_ADAPT : WIDTH_USED;
let AREA_HEIGHT_REST_ADAPT = !HORIZONTAL_LAYOUT ? HEIGHT_USED / AREA_COUNT_ADAPT : HEIGHT_USED;

let AREA_WIDTH_REST_ADAPT_ARR = [];
let AREA_HEIGHT_REST_ADAPT_ARR = [];
let AREA_WIDTH_REST_DIFF_ARR = [];
let AREA_HEIGHT_REST_DIFF_ARR = [];

AREA_COUNT_ADAPT = 0;
for (let i = 0; i < AREA_COUNT; i++) {
    let col = Math.floor(categoryGroups[i].count / MUTI_COL_THRESHOLD + 1);
    col = Math.min(col, 2);
    AREA_WIDTH_REST_ADAPT_ARR.push(col * AREA_WIDTH_REST_ADAPT);
    AREA_HEIGHT_REST_ADAPT_ARR.push(col * AREA_HEIGHT_REST_ADAPT);
    AREA_WIDTH_REST_DIFF_ARR.push(AREA_COUNT_ADAPT * AREA_WIDTH_REST_ADAPT);
    AREA_HEIGHT_REST_DIFF_ARR.push(AREA_COUNT_ADAPT * AREA_HEIGHT_REST_ADAPT);
    AREA_COUNT_ADAPT += col;
}

for (let i = 0; categoryGroupsOther.length != 0 && i < categoryGroupsOther[0].length; i++) {
    AREA_WIDTH_REST_ADAPT_ARR.push(WIDTH_MAX / nodesOther.length);
    AREA_HEIGHT_REST_ADAPT_ARR.push(HEIGHT_MAX / nodesOther.length);
}

for (let i = 0; i < links.length; i++) {
    //用于设置tip下标，用户唤醒tooltip
    links[i].tipIndex = i;

    let link = links[i];

    let sourceNode = nodeMap[link.source];

    //记录节点出口连线数量
    if (!sourceNode.outCount) {
        sourceNode.outCount = 0;
    }
    sourceNode.outCount++;

    //记录每个连线在起始节点节点类型中的排序序号。
    if (!categoryGroups[sourceNode.groupCategory].outIndex) {
        categoryGroups[sourceNode.groupCategory].outIndex = 0;
    }
    categoryGroups[sourceNode.groupCategory].outIndex++;
    link.categoryOutIndex = categoryGroups[sourceNode.groupCategory].outIndex;

    let targetNode = nodeMap[link.target];

    //记录节点入口连线数量
    if (!targetNode.inCount) {
        targetNode.inCount = 0;
    }
    targetNode.inCount++;

    //记录非相邻正向节点间的连线 在终点节点类型中的排序序号。
    if (sourceNode.groupCategory != targetNode.groupCategory + 1) {
        if (!categoryGroups[targetNode.groupCategory].inIndex) {
            categoryGroups[targetNode.groupCategory].inIndex = 0;
        }
        categoryGroups[targetNode.groupCategory].inIndex++;
        link.categoryInIndex = categoryGroups[targetNode.groupCategory].inIndex;

        if (!categoryGroupInArr[targetNode.groupCategory]) {
            categoryGroupInArr[targetNode.groupCategory] = [];
        }

        let categoryGroupInIndex = categoryGroupInArr[targetNode.groupCategory].indexOf(sourceNode.id);
        if (categoryGroupInIndex < 0) {
            link.categoryGroupInIndex = categoryGroupInArr[targetNode.groupCategory].push(sourceNode.id) - 1;
        }

        //记录全局的非相邻正向节点间的连线的排序序号
        if (!sourceNode.categoryOther && !targetNode.categoryOther) {
            sourceNode.flipIndex = flipIndex++;
            if (!sourceNode.flipFlag) {
                sourceNode.flipFlag = flipFlag++;
            }
        }
    }

    //绑定节点实体至连线上。
    link.sourceNode = sourceNode;
    link.targetNode = targetNode;
}

for (let i = 0; i < nodesOther.length; i++) {
    if (nodesOther[i].inCount || nodesOther[i].outCount) {
        nodesOther[i].categoryOtherIndex = flipIndexOther++;
    } else {
        nodesOther[i].categoryOtherIndex = 0;
    }
}

//节点的布局操作
function initNodeLayout() {
    for (let i = 0; i < nodes.length; i++) {
        let node = nodes[i];
        if (node.categoryOther) {
            continue;
        }
        //默认节点定位为居中模式
        let PLRate = AREA_REST_PADDING_RATE;
        //当节点数超过阈值，则节点按数量分列，并占用更多区域空间
        if (categoryGroups[node.groupCategory].count > MUTI_COL_THRESHOLD) {
            PLRate = AREA_REST_PADDING_RATE_WIDTH_MUTI_COL;
        }
        //当前类型节点分列数量
        let colCount = Math.floor(categoryGroups[node.groupCategory].count / MUTI_COL_THRESHOLD + 1);

        let xAxis = 0;
        let yAxis = 0;

        if (HORIZONTAL_LAYOUT) {//水平布局
            //每个分列所占空间大小
            let colSize = (1 - PLRate * 2) * AREA_WIDTH_REST_ADAPT_ARR[node.groupCategory] / colCount;
            //每个分列相隔距离
            let colDiff = (node.categoryIndex - 1) % colCount * colSize + colSize / 2;

            xAxis = AREA_WIDTH_REST_ADAPT_ARR[node.groupCategory] * PLRate + AREA_WIDTH_REST_DIFF_ARR[node.groupCategory] + AREA_PADDING_LEFT + colDiff;
            yAxis = AREA_HEIGHT_REST / categoryGroups[node.groupCategory].count * ((node.categoryIndex - 1) + 0.5) + AREA_PADDING_BOTTOM;

            node.x = xAxis;
            node.y = yAxis;
            //记录当前类型节点的最终部署位置
            node.value = [xAxis, yAxis];
            //记录当前类型节点的默认部署位置，用于连线布局
            node.dValue = [AREA_WIDTH_REST_ADAPT_ARR[node.groupCategory] * 0.5 + AREA_WIDTH_REST_DIFF_ARR[node.groupCategory] + AREA_PADDING_LEFT, yAxis];
        } else {//垂直布局
            //每个分列所占空间大小
            let colSize = (1 - PLRate * 2) * AREA_HEIGHT_REST_ADAPT_ARR[node.groupCategory] / colCount;
            //每个分列相隔距离
            let colDiff = (node.categoryIndex - 1) % colCount * colSize + colSize / 2;

            xAxis = AREA_WIDTH_REST / categoryGroups[node.groupCategory].count * ((node.categoryIndex - 1) + 0.5) + AREA_PADDING_LEFT;
            yAxis = AREA_HEIGHT_REST_ADAPT_ARR[node.groupCategory] * PLRate + AREA_HEIGHT_REST_DIFF_ARR[node.groupCategory] + AREA_PADDING_BOTTOM + colDiff;

            node.x = xAxis;
            node.y = yAxis;
            //记录当前类型节点的最终部署位置
            node.value = [xAxis, yAxis];
            //记录当前类型节点的默认部署位置，用于连线布局
            node.dValue = [xAxis, AREA_HEIGHT_REST_ADAPT_ARR[node.groupCategory] * 0.5 + AREA_HEIGHT_REST_DIFF_ARR[node.groupCategory] + AREA_PADDING_BOTTOM];
        }
    }

    for (let i = 0; i < nodesOther.length; i++) {
        let node = nodesOther[i];
        if (!node.categoryOther) {
            continue;
        }
        let xAxis = 0;
        let yAxis = 0;
        if (HORIZONTAL_LAYOUT) {//水平布局
            xAxis = (WIDTH_MAX / nodesOther.length) * (i + 0.5);
            yAxis = HEIGHT_MAX * (1 - PADDING_RATE) + HEIGHT_MAX * PADDING_RATE * 0.5;

            node.x = xAxis;
            node.y = yAxis;
            //记录当前类型节点的最终部署位置
            node.value = [xAxis, yAxis];
            //记录当前类型节点的默认部署位置，用于连线布局
            node.dValue = node.value;
        } else {
            xAxis = WIDTH_MAX * (1 - PADDING_RATE) + WIDTH_MAX * PADDING_RATE * 0.5;
            yAxis = (HEIGHT_MAX / nodesOther.length) * (i + 0.5);

            node.x = xAxis;
            node.y = yAxis;
            //记录当前类型节点的最终部署位置
            node.value = [xAxis, yAxis];
            //记录当前类型节点的默认部署位置，用于连线布局
            node.dValue = node.value;
        }
    }
}

initNodeLayout();

//节点的UI操作
function initNodeStyle() {
    for (let i = 0; i < nodes.length; i++) {
        let node = nodes[i]
        //如果节点没有连线，则默认缩小为2/3
        node.symbolSize = (node.inCount || node.outCount) ? getNodeSize(categoryGroups[node.groupCategory].count) :
            getNodeSize(categoryGroups[node.groupCategory].count) * 2 / 3;

        node.tooltip = {
            borderWidth: 3,
            triggerOn: 'click',
            position:
                {
                    right: 10,
                    top: 10
                },
            textStyle: {
                width: 500
            },
            formatter: function (params) {
                let props = [];
                if (NODE_PROPS) {
                    props = NODE_PROPS.split(",")
                } else {
                    for (let item in params.data.props) {
                        props.push(item);
                    }
                }

                let str1 = "<div style='min-width:200px;max-width:1000px;font-size:12px;text-align:right;word-wrap:break-word;white-space:normal;'>上报时间：" + params.data.lastUpdateTime + "</div>";

                for (let i = 0; i < props.length; i++) {
                    if (params.data.props && params.data.props[props[i]] !== undefined) {
                        let keyStr = KEY_FORMATTER[props[i]] ? KEY_FORMATTER[props[i]] : props[i];
                        let valStyle = VAL_FORMATTER[props[i]] ? VAL_FORMATTER[props[i]] : "";
                        let valStr = params.data.props[props[i]] instanceof Object ? JSON.stringify(params.data.props[props[i]]) : params.data.props[props[i]];
                        str1 += ("<label style='min-width:200px;text-align:right;font-weight:bold;vertical-align:top;'>" + keyStr + ":&nbsp;&nbsp;</label><label style='" + valStyle + ";min-width:300px;max-width:800px;text-align:left;word-wrap:break-word;white-space:pre-line;'>" + valStr + "</label><br/>");
                    }
                }


                let str2 = "";
                props = [];
                if (NODE_FIELD) {
                    props = NODE_FIELD.split(",")
                } else {
                    for (let item in params.data.fields) {
                        props.push(item);
                    }
                }

                for (let i = 0; i < props.length; i++) {
                    if (params.data.fields && params.data.fields[props[i]] !== undefined) {
                        let keyStr = KEY_FORMATTER[props[i]] ? KEY_FORMATTER[props[i]] : props[i];
                        let valStyle = VAL_FORMATTER[props[i]] ? VAL_FORMATTER[props[i]] : "";
                        let valStr = params.data.fields[props[i]] instanceof Object ? JSON.stringify(params.data.fields[props[i]]) : params.data.fields[props[i]];
                        str2 += ("<label style='min-width:200px;text-align:right;font-weight:bold;vertical-align:top;'>" + keyStr + ":&nbsp;&nbsp;</label><label style='" + valStr + "min-width:300px;max-width:800px;text-align:left;word-wrap:break-word;white-space:pre-line;'>" + valStr + "</label><br/>");
                    }
                }

                let str3 = (str2 != "" ? "<hr style='border: 1px dashed #999;'/>" : "");


                return "<div style='min-width: 400px;'>" + str1 + str3 + str2 + "</div>";
            }
        };

        node.itemStyle =
            {
                color: COLOR_SET[node.category]
            };

        let fontSize = 12;
        //如果同一类节点分列列数超过2列，则默认不显示文本
        if (categoryGroups[node.groupCategory].count > 40) {
            fontSize = 0;
        }

        node.label =
            {
                show: true,
                position: 'inside',
                color: '#FFF',
                fontSize: fontSize,
                formatter: function (item) {
                    if (!item.data.props || !item.data.props[NODE_LABEL]) {
                        return "";
                    }
                    let labels = item.data.props[NODE_LABEL].split('-');
                    if (labels.length < 2) {
                        return item.data.props[NODE_LABEL];
                    }
                    return labels[labels.length - 2].toUpperCase() + "-" + labels[labels.length - 1].toUpperCase();
                }
            };
    }
}

initNodeStyle();

//计算雷达节点的定位坐标。
function initRippleNode() {
    for (let i = 0; i < nodes.length; i++) {
        let node = nodes[i];

        let rippleEffectRed = node.nodeExpressions["rippleEffectRed"];
        let rippleEffectOrange = node.nodeExpressions["rippleEffectOrange"];
        if (!rippleEffectRed && !rippleEffectOrange) {
            continue;
        }

        //如果节点没有连线，则默认缩小为2/3
        let symbolSize = (node.inCount || node.outCount) ? getNodeSize(categoryGroups[node.groupCategory].count)
            : getNodeSize(categoryGroups[node.groupCategory].count) * 2 / 3;

        let rippleNode =
            {
                value: node.value,
                symbolSize: symbolSize
            };

        rippleNode.originSymbolSize = symbolSize;
        rippleNode.itemStyle =
            {
                normal: {
                    color: 'red',
                    shadowBlur: 0,
                    shadowColor: '#000',
                    opacity: 0
                }
            };
        //节点绑定雷达节点实体。
        node.rippleNode = rippleNode;

        let color;
        if (rippleEffectRed) {
            color = 'red';
        } else if (rippleEffectOrange) {
            color = 'orange';
        }

        let rippleNodeConfig = {
            type: 'effectScatter',
            zlevel: 2,
            data: [
                rippleNode
            ],
            showEffectOn: 'render',
            animationDuration: 0,
            rippleEffect: {
                brushType: 'stroke',
                color: color
            }
        }
        rippleNodesConfig.push(rippleNodeConfig);
        rippleNodes.push(rippleNode);
    }

}

initRippleNode();

function initLinkLayout() {
    for (let i = 0; i < links.length; i++) {
        let link = links[i];
        //设置连线动态效果样式
        let sourceNode = nodeMap[link.source];
        let targetNode = nodeMap[link.target];

        let coords = [];
        if (HORIZONTAL_LAYOUT) {//水平布局
            //定位连线初始坐标
            let diffPoint01 = 0;
            if (sourceNode.outCount) {
                if (!sourceNode.outCountUsed) {
                    sourceNode.outCountUsed = 0;
                }
                sourceNode.outCountUsed++;

                if (sourceNode.outCountUsed <= sourceNode.outCount) {
                    diffPoint01 = (sourceNode.outCountUsed - (sourceNode.outCount + 1.0) / 2) * Y_DIFF;
                }

                //偏移度增加限制
                if (Math.abs(diffPoint01) > sourceNode.symbolSize) {
                    diffPoint01 = 0;
                }
            }
            let point0 = [sourceNode.value[0], sourceNode.value[1] + diffPoint01];
            if (LESS_LINES) {
                point0 = [sourceNode.value[0], sourceNode.value[1] + 0];
            }

            //计算连线排线偏移度
            let diffPoint124 = link.categoryOutIndex * X_DIFF + X_DIFF / 2;
            if (LESS_LINES) {
                diffPoint124 = sourceNode.categoryIndex * X_DIFF + X_DIFF / 2;
            }

            let diffSLimit = AREA_WIDTH_REST_ADAPT_ARR[sourceNode.groupCategory] * AREA_REST_PADDING_RATE;
            if (categoryGroups[sourceNode.groupCategory].count > MUTI_COL_THRESHOLD) {
                diffSLimit = AREA_WIDTH_REST_ADAPT_ARR[sourceNode.groupCategory] * AREA_REST_PADDING_RATE_WIDTH_MUTI_COL;
            }
            //限制连线排线偏移度
            if (diffPoint124 > diffSLimit) {
                diffPoint124 = X_DIFF / 2;
            }

            //计算连线排线偏移度
            let diffXPoint56 = link.categoryInIndex * X_DIFF + X_DIFF / 2;
            let diffTLimit = AREA_WIDTH_REST_ADAPT_ARR[targetNode.groupCategory] * AREA_REST_PADDING_RATE;
            if (categoryGroups[targetNode.groupCategory].count > MUTI_COL_THRESHOLD) {
                diffTLimit = AREA_WIDTH_REST_ADAPT_ARR[targetNode.groupCategory] * AREA_REST_PADDING_RATE_WIDTH_MUTI_COL;
            }
            //限制连线排线偏移度
            if (diffXPoint56 > diffTLimit) {
                diffXPoint56 = X_DIFF / 2;
            }

            let point1 = [sourceNode.dValue[0] - AREA_WIDTH_REST_ADAPT_ARR[sourceNode.groupCategory] / 2 + diffPoint124, sourceNode.value[1] + diffPoint01];
            if (LESS_LINES) {
                point1 = [sourceNode.dValue[0] - AREA_WIDTH_REST_ADAPT_ARR[sourceNode.groupCategory] / 2 + diffPoint124, sourceNode.value[1] + 0];
            }

            let diffPoint236 = 0;
            if (targetNode.inCount) {
                if (!targetNode.inCountUsed) {
                    targetNode.inCountUsed = 0;
                }
                targetNode.inCountUsed++;

                if (targetNode.inCountUsed <= targetNode.inCount) {
                    diffPoint236 = (targetNode.inCountUsed - (targetNode.inCount + 1.0) / 2) * Y_DIFF;
                }

                //偏移度增加限制
                if (Math.abs(diffPoint236) > targetNode.symbolSize) {
                    diffPoint236 = 0;
                }
            }

            let point2 = [sourceNode.dValue[0] - AREA_WIDTH_REST_ADAPT_ARR[sourceNode.groupCategory] / 2 + diffPoint124, targetNode.value[1] - diffPoint236];
            let point3 = [targetNode.value[0], targetNode.value[1] - diffPoint236];

            let diffFlip = sourceNode.flipIndex / 2 * Y_DIFF + Y_DIFF / 2;
            //偏移度增加限制
            if (diffFlip > AREA_PADDING_BOTTOM) {
                diffFlip = Y_DIFF / 2;
            }
            let axis = ((sourceNode.flipFlag ? sourceNode.flipFlag : 0) % 2 === 0) ? diffFlip : HEIGHT_MAX * (1 - PADDING_RATE) - diffFlip;

            if (sourceNode.categoryOther) {
                axis = HEIGHT_MAX * (1 - PADDING_RATE) + (sourceNode.categoryOtherIndex - 1) * Y_DIFF;
            } else if (targetNode.categoryOther) {
                axis = HEIGHT_MAX * (1 - PADDING_RATE) + (targetNode.categoryOtherIndex - 1) * Y_DIFF;
            }

            let point4 = [sourceNode.dValue[0] - AREA_WIDTH_REST_ADAPT_ARR[sourceNode.groupCategory] / 2 + diffPoint124, axis];
            let point5 = [targetNode.dValue[0] + AREA_WIDTH_REST_ADAPT_ARR[targetNode.groupCategory] / 2 - diffXPoint56, axis];
            let point6 = [targetNode.dValue[0] + AREA_WIDTH_REST_ADAPT_ARR[targetNode.groupCategory] / 2 - diffXPoint56, targetNode.value[1] - diffPoint236];

            if (sourceNode.groupCategory === targetNode.groupCategory + 1) {
                //节点仅为相邻且连线方向为正向的排线
                coords = [point0, point1, point2, point3];
            } else {
                coords = [point0, point1, point4, point5, point6, point3];
            }
        } else {//垂直布局
            //定位连线初始坐标
            let diffPoint01 = 0;
            if (sourceNode.outCount) {
                if (!sourceNode.outCountUsed) {
                    sourceNode.outCountUsed = 0;
                }
                sourceNode.outCountUsed++;

                if (sourceNode.outCountUsed <= sourceNode.outCount) {
                    diffPoint01 = (sourceNode.outCountUsed - (sourceNode.outCount + 1.0) / 2) * X_DIFF;
                }

                //限制连线排线偏移度
                if (Math.abs(diffPoint01) > sourceNode.symbolSize) {
                    diffPoint01 = 0;
                }
            }
            let point0 = [sourceNode.value[0] + diffPoint01, sourceNode.value[1]];
            if (LESS_LINES) {
                point0 = [sourceNode.value[0] + 0, sourceNode.value[1]];
            }
            //计算连线排线偏移度
            let diffPoint124 = link.categoryOutIndex * Y_DIFF + Y_DIFF / 2;
            if (LESS_LINES) {
                diffPoint124 = link.categoryIndex * Y_DIFF + Y_DIFF / 2;
            }
            let diffSLimit = AREA_HEIGHT_REST_ADAPT_ARR[sourceNode.groupCategory] * AREA_REST_PADDING_RATE;
            if (categoryGroups[sourceNode.groupCategory].count > MUTI_COL_THRESHOLD) {
                diffSLimit = AREA_HEIGHT_REST_ADAPT_ARR[sourceNode.groupCategory] * AREA_REST_PADDING_RATE_WIDTH_MUTI_COL;
            }
            //限制连线排线偏移度
            if (diffPoint124 > diffSLimit) {
                diffPoint124 = Y_DIFF / 2;
            }

            //计算连线排线偏移度
            let diffPoint56 = link.categoryInIndex * Y_DIFF + Y_DIFF / 2;
            let diffTLimit = AREA_HEIGHT_REST_ADAPT_ARR[targetNode.groupCategory] * AREA_REST_PADDING_RATE;
            if (categoryGroups[sourceNode.groupCategory].count > MUTI_COL_THRESHOLD) {
                diffTLimit = AREA_HEIGHT_REST_ADAPT_ARR[targetNode.groupCategory] * AREA_REST_PADDING_RATE_WIDTH_MUTI_COL;
            }
            //限制连线排线偏移度
            if (diffPoint56 > diffTLimit) {
                diffPoint56 = Y_DIFF / 2;
            }

            let point1 = [sourceNode.value[0] + diffPoint01, sourceNode.dValue[1] - AREA_HEIGHT_REST_ADAPT_ARR[sourceNode.groupCategory] / 2 + diffPoint124];
            if (LESS_LINES) {
                point1 = [sourceNode.value[0] + 0, sourceNode.dValue[1] - AREA_HEIGHT_REST_ADAPT_ARR[sourceNode.groupCategory] / 2 + diffPoint124];
            }

            let diffPoint236 = 0;
            if (targetNode.inCount) {
                if (!targetNode.inCountUsed) {
                    targetNode.inCountUsed = 0;
                }
                targetNode.inCountUsed++;

                if (targetNode.inCountUsed <= targetNode.inCount) {
                    diffPoint236 = (targetNode.inCountUsed - (targetNode.inCount + 1.0) / 2) * X_DIFF;
                }

                //限制连线排线偏移度
                if (Math.abs(diffPoint236) > targetNode.symbolSize) {
                    diffPoint236 = 0;
                }
            }

            let point2 = [targetNode.value[0] - diffPoint236, sourceNode.dValue[1] - AREA_HEIGHT_REST_ADAPT_ARR[sourceNode.groupCategory] / 2 + diffPoint124];
            let point3 = [targetNode.value[0] - diffPoint236, targetNode.value[1]];

            let diffFlip = sourceNode.flipIndex / 2 * X_DIFF + X_DIFF / 2;
            //限制连线排线偏移度
            if (diffFlip > AREA_PADDING_LEFT) {
                diffFlip = X_DIFF / 2;
            }
            let axis = ((sourceNode.flipFlag ? sourceNode.flipFlag : 0) % 2 === 0) ? diffFlip : WIDTH_MAX * (1 - PADDING_RATE) - diffFlip;

            if (sourceNode.categoryOther) {
                axis = WIDTH_MAX * (1 - PADDING_RATE) + (sourceNode.categoryOtherIndex - 1) * X_DIFF;
            } else if (targetNode.categoryOther) {
                axis = WIDTH_MAX * (1 - PADDING_RATE) + (targetNode.categoryOtherIndex - 1) * X_DIFF;
            }

            let point4 = [axis, sourceNode.dValue[1] - (AREA_HEIGHT_REST_ADAPT_ARR[sourceNode.groupCategory]) * 1 / 2 + diffPoint124];
            let point5 = [axis, targetNode.dValue[1] + (AREA_HEIGHT_REST_ADAPT_ARR[targetNode.groupCategory]) * 1 / 2 - diffPoint56];
            let point6 = [targetNode.value[0] - diffPoint236, targetNode.dValue[1] + (AREA_HEIGHT_REST_ADAPT_ARR[targetNode.groupCategory]) * 1 / 2 - diffPoint56];

            if (sourceNode.groupCategory === targetNode.groupCategory + 1) {
                //节点仅为相邻且连线方向为正向的排线
                coords = [point0, point1, point2, point3];
            } else {
                coords = [point0, point1, point4, point5, point6, point3];
            }
        }

        let item = {
            coords: coords,
            link: link
        }

        linksData.push(item);

        //记录节点出口连线实例集合
        if (!outLinksData[sourceNode.id]) {
            outLinksData[sourceNode.id] = [];
        }
        outLinksData[sourceNode.id].push(item);

        //记录节点入口连线实例集合
        if (!inLinksData[targetNode.id]) {
            inLinksData[targetNode.id] = [];
        }
        inLinksData[targetNode.id].push(item);
    }
}

initLinkLayout();

function initLinkStyle() {
    for (let i = 0; i < linksData.length; i++) {
        let item = linksData[i];
        let link = item.link;
        item.tooltip =
            {
                borderWidth: 3,
                position: { right: 10, top: 10 },
                formatter: function (params) {
                    let props = [];
                    if (LINK_PROPS) {
                        props = LINK_PROPS.split(",")
                    } else {
                        for (let item in params.data.link.props) {
                            props.push(item);
                        }
                    }

                    let str1 = "<div style='min-width:200px;max-width:1000px;font-size:12px;text-align:right;word-wrap:break-word;white-space:normal;'>上报时间：" + params.data.link.lastUpdateTime + "</div>";

                    for (let i = 0; i < props.length; i++) {
                        if (params.data.link.props && params.data.link.props[props[i]] !== undefined) {
                            let keyStr = KEY_FORMATTER[props[i]] ? KEY_FORMATTER[props[i]] : props[i];
                            let valStyle = VAL_FORMATTER[props[i]] ? VAL_FORMATTER[props[i]] : "";
                            let valStr = params.data.link.props[props[i]] instanceof Object ? JSON.stringify(params.data.link.props[props[i]]) : params.data.link.props[props[i]];
                            str1 += ("<label style='min-width:200px;text-align:right;font-weight:bold;vertical-align:top;'>" + keyStr + ":&nbsp;&nbsp;</label><label style='" + valStyle + "min-width:300px;max-width:800px;text-align:left;word-wrap:break-word;white-space:pre-line'>" + valStr + "</label><br/>");
                        }
                    }

                    let str2 = "";
                    props = [];
                    if (LINK_FIELD) {
                        props = LINK_FIELD.split(",")
                    } else {
                        for (let item in params.data.link.fields) {
                            props.push(item);
                        }
                    }

                    for (let i = 0; i < props.length; i++) {
                        if (params.data.link.fields && params.data.link.fields[props[i]] !== undefined) {
                            let keyStr = KEY_FORMATTER[props[i]] ? KEY_FORMATTER[props[i]] : props[i];
                            let valStyle = VAL_FORMATTER[props[i]] ? VAL_FORMATTER[props[i]] : "";
                            let valStr = params.data.link.fields[props[i]] instanceof Object ? JSON.stringify(params.data.link.fields[props[i]]) : params.data.link.fields[props[i]];
                            str2 += ("<label style='min-width:200px;text-align:right;font-weight:bold;vertical-align:top;'>" + keyStr + ":&nbsp;&nbsp;</label><label style='" + valStyle + "min-width:300px;max-width:800px;text-align:left;word-wrap:break-word;white-space:pre-line'>" + valStr + "</label><br/>");
                        }
                    }


                    let str3 = str2 != "" ? "<hr style='border: 1px dashed #999;'/>" : "";

                    return "<div style='min-width: 400px;'>" + str1 + str3 + str2 + "</div>";
                }
            };

        //计算连线宽度，和节点大小有关
        let lineWidth = Math.min(nodeMap[link.source].symbolSize / LINK_SIZE_FACTOR, nodeMap[link.target].symbolSize / LINK_SIZE_FACTOR)
        item.lineWidth = lineWidth;

        let lineColor = COLOR_SET[Math.abs(COLOR_SET.length - 1 - nodeMap[link.source].categoryIndex) % COLOR_SET.length];

        item.lineStyle =
            {
                type: 'dashed',
                width: lineWidth,
                color: lineColor,
                opacity: 0.7
            };

        //设置是否隐藏连线效果
        if (link.linkExpressions["linkSolid"]) {
            item.lineStyle.type = 'solid';
            item.lineStyle.sourceType = 'solid';
        } else {
            item.lineStyle.type = 'dashed';
            item.lineStyle.sourceType = 'dashed';
        }

        item.effect =
            {
                show: USE_LINK_EFFECT,
            };

        //设置是否隐藏连线效果
        if (link.linkExpressions["linkEffectHidden"]) {
            item.effect.symbolSize = 0;
            item.effect.sourceSymbolSize = 0;
        } else {
            item.effect.symbolSize = LINK_EFFECT_SIZE;
            item.effect.sourceSymbolSize = LINK_EFFECT_SIZE;
        }

        //设置连线效果样式
        if (link.linkExpressions["linkEffectGreen"]) {
            item.effect.color = 'green';
        } else if (link.linkExpressions["linkEffectOrange"]) {
            item.effect.color = 'orange';
        } else if (link.linkExpressions["linkEffectRed"]) {
            item.effect.color = 'red';
        }
    }
}

initLinkStyle();

let options = {
    backgroundColor: '#181b1f',
    tooltip: {
        triggerOn: 'mousemove',
        enterable: true,
        hideDelay: 5000
    },
    xAxis: {
        min: 0,
        max: WIDTH_MAX,
        show: false,
        type: 'value'
    },
    yAxis: {
        min: 0,
        max: HEIGHT_MAX,
        show: false,
        type: 'value'
    },
    color: COLOR_SET,
    legend: [{
        show: true,
        data: categories.map(function (category, i) {
            return {
                name: category.name,
                icon: 'circle',

                itemStyle: {
                    color: COLOR_SET[i]
                }
            };
        }),
        orient: 'vertical',
        left: 0,
        textStyle: { color: 'white' }
    }],
    series: [{
        type: 'graph',
        zlevel: 3,
        coordinateSystem: 'cartesian2d',
        roam: true,
        animationType: 'scale',
        animationEasing: 'elasticOut',
        animationDuration: 0,
        data: nodes,
        categories: categories,
        //links: links
    }, {
        type: 'lines',
        zlevel: 1,
        polyline: true,
        coordinateSystem: 'cartesian2d',
        roam: true,
        lineStyle: {
            type: 'solid',
            width: 1,
            color: 'orange',
            curveness: 0.3
        },
        emphasis: {
            focus: 'none',
            lineStyle: {
                type: 'solid',
                width: 5
            }
        },
        effect: {
            show: USE_LINK_EFFECT,
            period: 5,
            constantSpeed: 50,
            trailLength: 0.3,
            symbol: 'arrow',
            color: '#fff',
            symbolSize: LINK_EFFECT_SIZE
        },
        data: linksData
    }]
};

for (let i = 0; i < rippleNodesConfig.length; i++) {
    options.series.push(rippleNodesConfig[i]);
}

var eventFlag = 1;

function initEvent() {
    echartsInstance.off('mouseover');
    echartsInstance.on('mouseover', function (params) {
        if (params.componentSubType != "graph") {
            return false;
        }

        let node = nodeMap[params.data.id];

        //淡出其他节点
        for (let id in nodeMap) {
            nodeMap[id].itemStyle.opacity = 0.1;
        }

        //淡出其他连线
        for (let id in linksData) {
            linksData[id].lineStyle.opacity = 0.05;
            linksData[id].effect.symbolSize = 0;
        }

        //淡出其他雷达节点
        for (let id in rippleNodes) {
            rippleNodes[id].symbolSize = 0;
        }

        //保留本身节点
        node.itemStyle.opacity = 1;
        //保留本身雷达节点
        if (node.rippleNode) {
            node.rippleNode.symbolSize = node.rippleNode.originSymbolSize;
        }

        if (inLinksData[node.id]) {
            for (let i = 0; i < inLinksData[node.id].length; i++) {
                //高亮本身节点相邻的连线
                let inLink = inLinksData[node.id][i];
                inLink.lineStyle.width = 2;
                inLink.lineStyle.type = "solid";
                inLink.lineStyle.opacity = 0.7;
                inLink.effect.symbolSize = inLink.effect.sourceSymbolSize;

                //保留相邻节点
                let sourceNode = nodeMap[inLink.link.source];
                sourceNode.itemStyle.opacity = 1;
                //保留相邻雷达节点
                if (sourceNode.rippleNode) {
                    sourceNode.rippleNode.symbolSize = sourceNode.rippleNode.originSymbolSize;
                }
            }
        }

        if (outLinksData[node.id]) {
            for (let i = 0; i < outLinksData[node.id].length; i++) {
                //高亮本身节点相邻的连线
                let outLink = outLinksData[node.id][i];
                outLink.lineStyle.width = 2;
                outLink.lineStyle.type = "solid";
                outLink.lineStyle.opacity = 0.7;
                outLink.effect.symbolSize = outLink.effect.sourceSymbolSize;

                //保留相邻节点
                let targetNode = nodeMap[outLink.link.target];
                targetNode.itemStyle.opacity = 1;
                //保留相邻雷达节点
                if (targetNode.rippleNode) {
                    targetNode.rippleNode.symbolSize = targetNode.rippleNode.originSymbolSize;
                }
            }
        }
        //重新渲染
        echartsInstance.setOption(options, false, false);
    });


    echartsInstance.off('mouseout');
    echartsInstance.on('mouseout', function (params) {
        if (params.componentSubType != "graph") {
            return false;
        }

        let node = nodeMap[params.data.id];

        //显示所有节点
        for (let id in nodeMap) {
            nodeMap[id].itemStyle.opacity = 1;
        }

        //显示所有连线
        for (let id in linksData) {
            linksData[id].lineStyle.opacity = 0.7;
            linksData[id].effect.symbolSize = linksData[id].effect.sourceSymbolSize;
        }

        //显示所有雷达节点
        for (let id in rippleNodes) {
            rippleNodes[id].symbolSize = rippleNodes[id].originSymbolSize;
        }

        if (inLinksData[node.id]) {
            for (let i = 0; i < inLinksData[node.id].length; i++) {
                //还原本身节点相邻的连线
                let inLink = inLinksData[node.id][i];
                inLink.lineStyle.width = inLink.lineWidth;
                inLink.lineStyle.type = inLink.lineStyle.sourceType;
            }
        }

        if (outLinksData[node.id]) {
            for (let i = 0; i < outLinksData[node.id].length; i++) {
                //还原本身节点相邻的连线
                let outLink = outLinksData[node.id][i];
                outLink.lineStyle.width = outLink.lineWidth;
                outLink.lineStyle.type = outLink.lineStyle.sourceType;
            }
        }

        //重新渲染
        echartsInstance.setOption(options, false, false);
    });

    eventFlag = 1;
}

initEvent();

//添加这个样式是为了可以复制tooltip中文本内容
$("<style type='text/css'> *{-webkit-user-select: text!important; user-select: text!important;} </style>").appendTo("head");

return options;
