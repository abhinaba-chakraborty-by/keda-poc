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

  ngOnInit(): void {
    this.timer.subscribe(value => {
      this.serviceBusAdministrationClient.getQueueRuntimeProperties(this.queueName)
        .then((response) => {
          this.msgCount = response.activeMessageCount;
        });
    });
  }

}
