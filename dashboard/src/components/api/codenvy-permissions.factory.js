/*
 *  [2015] - [2016] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
'use strict';

/**
 * This class is handling the permissions API.
 *
 * @author Ann Shumilova
 */
export class CodenvyPermissions {

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor($q, $resource) {
    this.$q = $q;
    this.$resource = $resource;

    this.workspacePermissions = new Map();

    // remote call
    this.remotePermissionsAPI = this.$resource('/api/permissions', {}, {
      store: {method: 'POST', url: '/api/permissions'},
      remove: {method: 'DELETE', url: '/api/permissions/:domain/:instance/:user'},
      getPermissionsByInstance: {method: 'GET', url: '/api/permissions/:domain/:instance/list', isArray: true}
    });
  }

  /**
   * Stores permissions data.
   *
   * @param data - permissions data
   * @returns {*}
   */
  storePermissions(data) {
    let promise = this.remotePermissionsAPI.store(data).$promise;
    return promise;
  }

  /**
   * Fetch workspace permissions
   *
   * @param workspaceId workspace id
   * @returns {*}
   */
  fetchWorkspacePermissions(workspaceId) {
    let promise = this.remotePermissionsAPI.getPermissionsByInstance({domain: 'workspace', instance: workspaceId}).$promise;
    let resultPromise = promise.then((permissions) => {
        this.workspacePermissions.set(workspaceId, permissions);
      });

    return resultPromise;
  }

  /**
   * Returns permissions data by workspace id
   *
   * @param workspaceId workspace id
   * @returns {*} list of workspace permissions
   */
  getWorkspacePermissions(workspaceId) {
    return this.workspacePermissions.get(workspaceId);
  }

  /**
   * Remove permission for pointed user in pointed workspace.
   *
   * @param workspaceId workspace id
   * @param userId user id
   * @returns {$promise} request promise
   */
  removeWorkspacePermissions(workspaceId, userId) {
    let promise = this.remotePermissionsAPI.remove({domain: 'workspace', instance: workspaceId, user: userId}).$promise;
    return promise;
  }
}