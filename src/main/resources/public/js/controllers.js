angular.module('Optimizer.controllers', [ 'ngFileUpload', 'chart.js' ]).
controller('appController', ['$scope', '$http', 'Upload', function ($scope, $http, Upload) {
    console.log("app Controller on load.")
    $scope.playerLists = [];
    $scope.pgs = [];
    $scope.sgs = [];
    $scope.gs = [];
    $scope.pfs = [];
    $scope.sfs = [];
    $scope.fs = [];
    $scope.cs = [];
    $scope.utils = [];
    $scope.hideUpload = false;
    $scope.selectedPosition = "pg";
    $scope.exportAll = true;
    $scope.positions = [{name: "Point Guard", code: "PG"},{name: "Shooting Guard", code: "SG"},{name: "Guard", code: "G"},
    {name: "Small Forward", code: "SF"},{name: "Power Forward", code: "PF"},{name: "Forward", code: "F"},
    {name: "Center", code: "C"},{name: "Utility", code: "UTIL"}];
    $scope.canExport = false;
    $scope.distribution = {
        "player_names": [],
        "player_counts": []
    };
    $scope.holdAreaTeams = [];
    $scope.showDistribution = true;
    $scope.configurationMode = true;
    $scope.configureGeneration = true;
    $scope.submit = function() {
        if ($scope.form.file.$valid && $scope.file) {
            $scope.upload($scope.file);
        }
    };

    $scope.upload = function (file) {
        Upload.upload({
            url: '/parseCsv',
            data: {file: file, 'title': $scope.title ? $scope.title : "temporary"}
        }).then(function (resp) {
            $scope.hideUpload = true;
            $scope.players = resp.data.players;
            console.log($scope.players);
            $scope.getPositionLists();
        }, function (resp) {
            console.log('Error status: ' + resp.status);
        }, function (evt) {
            var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
            console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
        });
    };

    $scope.getPlayerIdList = function(file, success, failure) {
        console.log('blah');
        console.log(file);
        Upload.upload({
            url: '/parseIdCsv',
            data: {file: file, 'title': $scope.title ? $scope.title : "temporary"}
        }).then(function (resp) {
            success(resp);
        }, function (resp) {
            failure(resp);
        }, function (evt) {
            var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
            console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
        });
    };

    $scope.onPositionSelected = function() {
        if ($scope.selectedPosition === "Point Guard") {
            $scope.selectedList = $scope.pgs;
        }
        else if ($scope.selectedPosition === "Shooting Guard") {
            $scope.selectedList = $scope.sgs;
        }
        else if ($scope.selectedPosition === "Guard") {
            $scope.selectedList = $scope.gs;
        }
        else if ($scope.selectedPosition === "Small Forward") {
            $scope.selectedList = $scope.sfs;
        }
        else if ($scope.selectedPosition === "Power Forward") {
            $scope.selectedList = $scope.pfs;
        }
        else if ($scope.selectedPosition === "Forward") {
            $scope.selectedList = $scope.fs;
        }
        else if ($scope.selectedPosition === "Center") {
            $scope.selectedList = $scope.cs;
        }
        else if ($scope.selectedPosition === "Utility") {
            $scope.selectedList = $scope.utils;
        }
    }

    $scope.$watch(
        "selectedPosition",
        function onPositionSelected(newVal, oldVal) {
            console.log("old: " + oldVal);
            console.log("new: " + newVal);
        }
    );

    $scope.increaseProjectedPoints = function(player) {
        player.pts = (player.pts + 1);
        player.ratio = ((player.pts / player.salary) * 10000).toFixed(3);;
        player.value_ratio = (player.ratio * player.pts);
    }

    $scope.decreaseProjectedPoints = function(player) {
        player.pts = (player.pts - 1);
        player.ratio = ((player.pts / player.salary) * 10000).toFixed(3);
        player.value_ratio = (player.ratio * player.pts);
    }

    $scope.getPositionLists = function() {
        $scope.pgs = [];
        $scope.sgs = [];
        $scope.gs = [];
        $scope.sfs = [];
        $scope.pfs = [];
        $scope.fs = [];
        $scope.cs = [];
        $scope.utils = [];
        for (var i = 0; i < $scope.players.length; i++) {
            var player = $scope.players[i];
            if (player.position.indexOf("/") > -1) {
                var pos1 = player.position.split("/")[0];
                var pos2 = player.position.split("/")[1];
                var g = false, f = false;

                if (pos1 === "PG" || pos2 === "PG") {
                    $scope.pgs.push(player);
                    g = true;
                }
                if (pos1 === "SG" || pos2 === "SG") {
                    $scope.sgs.push(player);
                    g = true;
                }
                if (pos1 === "SF" || pos2 === "SF") {
                    $scope.sfs.push(player);
                    f = true;
                }
                if (pos1 === "PF" || pos2 === "PF") {
                    $scope.pfs.push(player);
                    f = true;
                }
                if (pos1 === "C" || pos2 === "C") {
                    $scope.cs.push(player);
                }

                if (g) {
                    $scope.gs.push(player);
                }
                if (f) {
                    $scope.fs.push(player);
                }
            }
            else {
                if (player.position === "PG") {
                    $scope.pgs.push(player);
                    $scope.gs.push(player);
                }
                else if (player.position === "SG") {
                    $scope.sgs.push(player);
                    $scope.gs.push(player);
                }
                else if (player.position === "SF") {
                    $scope.sfs.push(player);
                    $scope.fs.push(player);
                }
                else if (player.position === "PF") {
                    $scope.pfs.push(player);
                    $scope.fs.push(player);
                }
                else if (player.position === "C") {
                    $scope.cs.push(player);
                }
            }
            $scope.utils.push(player);
        }
        console.log($scope.pgs);
        console.log($scope.sgs);
        console.log($scope.gs);
        console.log($scope.pfs);
        console.log($scope.sfs);
        console.log($scope.fs);
        console.log($scope.cs);
        console.log($scope.utils);
    }

    $scope.getRestrictionsAndGenerate = function() {
        $scope.getRestrictions();
        $scope.generateTeams();
    }

    $scope.getRestrictions = function() {
        var restrictions = [];
        for (var i = 0; i < $scope.players.length; i++) {
            if ($scope.players[i].restriction) {
                restrictions.push({
                    "player": $scope.players[i],
                    "percentage": parseInt($scope.players[i].restriction)
                });
            }
        }
        $scope.restrictions = restrictions;
    }

    $scope.generateTeams = function() {
        var lists = [$scope.pgs, $scope.sgs, $scope.sfs, $scope.pfs, $scope.cs, $scope.gs, $scope.fs, $scope.utils];

        $scope.bestTeams = [];
        $scope.generationInProgress = true;

        var data = {
            "lists": lists,
            "restrictions": $scope.restrictions,
            "universal_max": parseInt($scope.universalMax) ? parseInt($scope.universalMax) : 0,
            "batch_size": parseInt($scope.batchSize) ? parseInt($scope.batchSize) : 0
        }

        $http.post('/generate', JSON.stringify(data), {}).then(function(response) {
            $scope.bestTeams = response.data.teams;

            $scope.generationInProgress = false;
            $scope.configureGeneration = false;

            for (var i = 0; i < $scope.bestTeams.length; i++) {
                var total = 0;
                for (var j = 0; j < $scope.bestTeams[i].roster.length; j++) {
                    total += $scope.bestTeams[i].roster[j].salary;
                }
                $scope.bestTeams[i].total_salary = total;
            }
            $scope.distribution = {
                "player_names": [],
                "player_counts": []
            }
            for (var i = 0; i < response.data.selected_distribution.length; i++) {
                var dist = response.data.selected_distribution[i];
                $scope.distribution.player_names.push(dist.name);
                $scope.distribution.player_counts.push(dist.count);
            }

            $scope.selectAllForExport();

        }, function(response) {
            console.log("Error response.");
            console.log(response);
        });
    }

    $scope.selectAllForExport = function() {
        for (var i = 0; i < $scope.bestTeams.length; i++) {
            $scope.bestTeams[i].export = $scope.exportAll;
        }
        $scope.updateCsvData();
    }

    $scope.updateCsvData = function() {
        console.log('getting csv data for export');

        console.log('exporting teams...');
        var exports = [];
        for (var i = 0; i < $scope.bestTeams.length; i++) {
            if ($scope.bestTeams[i].export) {
                exports.push($scope.bestTeams[i]);
            }
        }
        console.log(exports);
        var positions = ["a", "b", "c", "d", "e", "f", "g", "h"];
        var exportData = [];
        for (var i = 0; i < exports.length; i++) {
            exports[i].csv = "";
            var count = 0;
            var data = {
                "a": "",
                "b": "",
                "c": "",
                "d": "",
                "e": "",
                "f": "",
                "g": "",
                "h": ""
            }
            //  0   1  2   3   4  5  6    7
            // pg, sg, g, sf, pf, f, c, util
            // pg, sg, sf, pf, c, g, f, util
            //  0   1   3   4  6  2  5    7
            // start swap

            var temp = exports[i][2];
            exports[i][2] = exports[i][3];
            exports[i][3] = exports[i][4];
            exports[i][4] = exports[i][6];
            exports[i][6] = exports[i][5];
            exports[i][5] = temp;
            // finish swap
            console.log(exports[i]);
            for (var j = 0; j < exports[i].roster.length; j++) {
                exports[i].csv += $scope.playerIdMap[exports[i].roster[j].id];
                console.log(exports[i].roster[j].id);

                if (count < exports[i].roster.length - 1) {
                    exports[i].csv += ",";
                }
                data[positions[count]] = $scope.playerIdMap[exports[i].roster[j].id]

                console.log(data);
                count++;

            }
            exportData.push(data);
        }
        $scope.exports = exports;
        $scope.exportData = exportData;
        console.log("exportData:")
        console.log(exportData)
        console.log(exports);
        console.log($scope.exports);
//        if ($scope.exportData.length > 0) {
//            $scope.canExport = true;
//        }
//        else {
//            $scope.canExport = false;
//        }
    }

    //<button style="float: left;" ng-click="addToHoldArea()">Hold</button>
    //<button style="float: left;" ng-click="clearHoldArea()">Clear</button>

    $scope.addToHoldArea = function() {
        $scope.holdAreaTeams = $scope.holdAreaTeams.concat($scope.exportData);
        console.log("post concat: ");
        console.log($scope.holdAreaTeams);
        console.log($scope.exportData);
        $scope.canExport = $scope.holdAreaTeams.length > 0;
    }

    $scope.clearHoldArea = function() {
        $scope.canExport = false;
        $scope.holdAreaTeams = [];
    }

    $scope.exportTeams = function() {
        if ($scope.exportForm.exportFile.$valid && $scope.exportFile) {
            $scope.getPlayerIdList($scope.exportFile, function(resp) {
                $scope.playerIdMap = resp.data;
                console.log($scope.playerIdMap);
                $scope.gotMappings = true;
                $scope.selectAllForExport();
            }, function(resp) {
                console.log("Error response:");
                console.log(resp);
            });
        }
    }
}]);