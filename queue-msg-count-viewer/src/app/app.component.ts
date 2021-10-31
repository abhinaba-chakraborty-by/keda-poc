import { Component, OnInit } from '@angular/core';
import { timer } from "rxjs";
import { ServiceBusAdministrationClient } from "@azure/service-bus";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'queue-msg-count-viewer';
  msgCount: any;
  queueName = "item-ingestion-queue"
  connectionString: string = "";
  timer = timer(500, 1000);
  serviceBusAdministrationClient = new ServiceBusAdministrationClient(this.connectionString);
  view: any[] = [200, 400];
  gradient = false;
  showXAxis = true;
  showYAxis = true;
  showXAxisLabel = false;
  showYAxisLabel = true;
  xAxisLabel = 'item-ingestion-queue';
  yAxisLabel = 'Message count';
  tooltipDisabled = true;
  colorScheme = {
    domain: ['#5AA454', '#A10A28', '#C7B42C', '#AAAAAA']
  };
  showGridLines = false;
  data: any[] = [];

  ngOnInit(): void {
    this.timer.subscribe(value => {
      this.serviceBusAdministrationClient.getQueueRuntimeProperties(this.queueName)
        .then((response) => {
          this.data = [
            {
              "name": "Queue",
              "value": response.activeMessageCount
            }
          ]
          this.msgCount = response.activeMessageCount;
        });
    });
  }

}
