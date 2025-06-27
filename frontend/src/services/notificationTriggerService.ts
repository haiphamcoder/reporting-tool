import React from 'react';
import { useNotifications } from '../context/NotificationContext';

export interface NotificationTrigger {
  type: 'success' | 'error' | 'warning' | 'info';
  category: 'report' | 'chart' | 'source' | 'user' | 'system';
  title: string;
  message: string;
}

class NotificationTriggerService {
  private static instance: NotificationTriggerService;
  private notificationContext: any = null;

  private constructor() {}

  public static getInstance(): NotificationTriggerService {
    if (!NotificationTriggerService.instance) {
      NotificationTriggerService.instance = new NotificationTriggerService();
    }
    return NotificationTriggerService.instance;
  }

  public setNotificationContext(context: any) {
    this.notificationContext = context;
  }

  private async triggerNotification(trigger: NotificationTrigger) {
    if (this.notificationContext?.addNotification) {
      try {
        await this.notificationContext.addNotification(trigger);
      } catch (error) {
        console.error('Failed to trigger notification:', error);
      }
    }
  }

  // Report notifications
  public async reportCreated(reportName: string) {
    await this.triggerNotification({
      type: 'success',
      category: 'report',
      title: 'Report Created',
      message: `Report "${reportName}" has been created successfully`
    });
  }

  public async reportUpdated(reportName: string) {
    await this.triggerNotification({
      type: 'success',
      category: 'report',
      title: 'Report Updated',
      message: `Report "${reportName}" has been updated successfully`
    });
  }

  public async reportDeleted(reportName: string) {
    await this.triggerNotification({
      type: 'info',
      category: 'report',
      title: 'Report Deleted',
      message: `Report "${reportName}" has been deleted`
    });
  }

  public async reportExportStarted(reportName: string) {
    await this.triggerNotification({
      type: 'info',
      category: 'report',
      title: 'Report Export Started',
      message: `Exporting report "${reportName}"...`
    });
  }

  public async reportExportCompleted(reportName: string, format: string) {
    await this.triggerNotification({
      type: 'success',
      category: 'report',
      title: 'Report Export Completed',
      message: `Report "${reportName}" has been exported as ${format.toUpperCase()}`
    });
  }

  public async reportExportFailed(reportName: string, error: string) {
    await this.triggerNotification({
      type: 'error',
      category: 'report',
      title: 'Report Export Failed',
      message: `Failed to export report "${reportName}": ${error}`
    });
  }

  // Chart notifications
  public async chartCreated(chartName: string) {
    await this.triggerNotification({
      type: 'success',
      category: 'chart',
      title: 'Chart Created',
      message: `Chart "${chartName}" has been created successfully`
    });
  }

  public async chartUpdated(chartName: string) {
    await this.triggerNotification({
      type: 'success',
      category: 'chart',
      title: 'Chart Updated',
      message: `Chart "${chartName}" has been updated successfully`
    });
  }

  public async chartDeleted(chartName: string) {
    await this.triggerNotification({
      type: 'info',
      category: 'chart',
      title: 'Chart Deleted',
      message: `Chart "${chartName}" has been deleted`
    });
  }

  public async chartCreationFailed(chartName: string, error: string) {
    await this.triggerNotification({
      type: 'error',
      category: 'chart',
      title: 'Chart Creation Failed',
      message: `Failed to create chart "${chartName}": ${error}`
    });
  }

  public async chartDataRefreshed(chartName: string) {
    await this.triggerNotification({
      type: 'success',
      category: 'chart',
      title: 'Chart Data Refreshed',
      message: `Data for chart "${chartName}" has been refreshed`
    });
  }

  // Source notifications
  public async sourceAdded(sourceName: string, sourceType: string) {
    await this.triggerNotification({
      type: 'success',
      category: 'source',
      title: 'Data Source Added',
      message: `${sourceType} source "${sourceName}" has been added successfully`
    });
  }

  public async sourceUpdated(sourceName: string) {
    await this.triggerNotification({
      type: 'success',
      category: 'source',
      title: 'Data Source Updated',
      message: `Data source "${sourceName}" has been updated successfully`
    });
  }

  public async sourceDeleted(sourceName: string) {
    await this.triggerNotification({
      type: 'info',
      category: 'source',
      title: 'Data Source Deleted',
      message: `Data source "${sourceName}" has been deleted`
    });
  }

  public async sourceConnectionWarning(sourceName: string, warning: string) {
    await this.triggerNotification({
      type: 'warning',
      category: 'source',
      title: 'Data Source Warning',
      message: `Warning for "${sourceName}": ${warning}`
    });
  }

  public async sourceConnectionError(sourceName: string, error: string) {
    await this.triggerNotification({
      type: 'error',
      category: 'source',
      title: 'Data Source Error',
      message: `Connection error for "${sourceName}": ${error}`
    });
  }

  public async sourceDataRefreshed(sourceName: string, recordCount: number) {
    await this.triggerNotification({
      type: 'success',
      category: 'source',
      title: 'Data Refreshed',
      message: `Data source "${sourceName}" refreshed with ${recordCount} records`
    });
  }

  // User notifications
  public async userAdded(userEmail: string, role: string) {
    await this.triggerNotification({
      type: 'success',
      category: 'user',
      title: 'User Added',
      message: `User ${userEmail} has been added with role: ${role}`
    });
  }

  public async userUpdated(userEmail: string) {
    await this.triggerNotification({
      type: 'success',
      category: 'user',
      title: 'User Updated',
      message: `User ${userEmail} has been updated`
    });
  }

  public async userDeleted(userEmail: string) {
    await this.triggerNotification({
      type: 'info',
      category: 'user',
      title: 'User Deleted',
      message: `User ${userEmail} has been deleted`
    });
  }

  public async userRoleChanged(userEmail: string, newRole: string) {
    await this.triggerNotification({
      type: 'info',
      category: 'user',
      title: 'User Role Changed',
      message: `User ${userEmail} role changed to: ${newRole}`
    });
  }

  // System notifications
  public async systemMaintenance(scheduledTime: string, duration: string) {
    await this.triggerNotification({
      type: 'warning',
      category: 'system',
      title: 'System Maintenance',
      message: `Scheduled maintenance at ${scheduledTime} for ${duration}`
    });
  }

  public async systemUpdate(version: string) {
    await this.triggerNotification({
      type: 'info',
      category: 'system',
      title: 'System Update',
      message: `System updated to version ${version}`
    });
  }

  public async securityAlert(alert: string) {
    await this.triggerNotification({
      type: 'error',
      category: 'system',
      title: 'Security Alert',
      message: alert
    });
  }

  public async performanceWarning(warning: string) {
    await this.triggerNotification({
      type: 'warning',
      category: 'system',
      title: 'Performance Warning',
      message: warning
    });
  }

  // Custom notification
  public async customNotification(trigger: NotificationTrigger) {
    await this.triggerNotification(trigger);
  }
}

// Export singleton instance
export const notificationTriggerService = NotificationTriggerService.getInstance();

// Hook to use notification triggers
export function useNotificationTriggers() {
  const notificationContext = useNotifications();
  
  // Set the context in the service
  React.useEffect(() => {
    notificationTriggerService.setNotificationContext(notificationContext);
  }, [notificationContext]);

  return notificationTriggerService;
} 