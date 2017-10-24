//@define Ext.calendar.data.EventMappings
/**
 * @class Ext.calendar.data.EventMappings
 * @extends Object
 * A simple object that provides the field definitions for Event records so that they can be easily overridden.
 */
Ext.ns('Ext.calendar.data');

Ext.calendar.data.EventMappings = {
    EventId: {
        name: 'EventId',
        mapping: 'id',
        type: 'int'
    },
    CalendarId: {
        name: 'CalendarId',
        mapping: 'cid',
        type: 'int'
    },
    Title: {
        name: 'Title',
        mapping: 'title',
        type: 'string'
    },
    StartDate: {
        name: 'StartDate',
        mapping: 'start',
        type: 'date',
        dateFormat: 'c'
    },
    EndDate: {
        name: 'EndDate',
        mapping: 'end',
        type: 'date',
        dateFormat: 'c'
    },
    IsAllDay: {
        name: 'IsAllDay',
        mapping: 'ad',
        type: 'boolean'
    },
    Type: {
        name: 'type',
        mapping: 'type',
        type: 'string'
    },
    Customer: {
        name: 'customer',
        mapping: 'customer',
        type: 'string'
    },
    DiagnoseType:{
        name: 'diagnoseType',
        mapping: 'diagnoseType',
        type: 'string'
    },
    Remark:{
        name: 'remark',
        mapping: 'remark',
        type: 'string'
    },
    Reminder: {
        name: 'Reminder',
        mapping: 'rem',
        type: 'string'
    },
    IsNew: {
        name: 'IsNew',
        mapping: 'n',
        type: 'boolean'
    }
};
