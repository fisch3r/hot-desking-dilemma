'use strict';

angular.module('hotdesk.version', [
  'hotdesk.version.interpolate-filter',
  'hotdesk.version.version-directive'
])

.value('version', '0.1');
