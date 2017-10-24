Ext.define('Ext.app.ChartPortlet', {

    extend: 'Ext.panel.Panel',
    alias: 'widget.chartportlet',

    requires: [
        'Ext.data.JsonStore',
        'Ext.chart.theme.Base',
        'Ext.chart.series.Series',
        'Ext.chart.series.Line',
        'Ext.chart.axis.Numeric'
    ],

    generateData: function(){
        var data = [{
                name: 0,
                djia: 10000,
                sp500: 1100
            }],
            i;
        for (i = 1; i < 50; i++) {
            data.push({
                name: i,
                sp500: data[i - 1].sp500 + ((Math.floor(Math.random() * 2) % 2) ? -1 : 1) * Math.floor(Math.random() * 7),
                djia: data[i - 1].djia + ((Math.floor(Math.random() * 2) % 2) ? -1 : 1) * Math.floor(Math.random() * 7)
            });
        }
        return data;
    },
	
    initComponent: function(){
        var store = Ext.create('Ext.data.JsonStore', {
            fields: ['value'],
            data: [
                { 'value':80 }
            ]
        });
        Ext.apply(this, {
            store: store,
            animate: true,
            insetPadding: 30,
            axes: [{
                type: 'gauge',
                position: 'gauge',
                minimum: 0,
                maximum: 100,
                steps: 10,
                margin: 10
            }],
            series: [{
                type: 'gauge',
                field: 'value',
                donut: 30,
                colorSet: ['#F49D10', '#ddd']
            }]
        });
        this.callParent(arguments);
    }
});
