import { Component, OnInit } from '@angular/core';
import {IndexerService} from '../indexer.service';
import {IndexableItem} from '../indexable.item';

@Component({
  selector: 'app-files-indexer',
  templateUrl: './files-indexer.component.html',
  styleUrls: ['./files-indexer.component.css']
})
export class FilesIndexerComponent implements OnInit {

  selectedFiles: FileList;

  constructor(private indexerService: IndexerService) { }

  ngOnInit() {
  }

  selectFile(event) {
    this.selectedFiles = event.target.files;
  }

  index(rows: string): void {
    const row = rows.split('\n');
    const indexableItems = row.map(this.getIndexableItem);
    for (let obj in this.selectedFiles) {
      console.log(`File: ${obj}`);
    }
    indexableItems.forEach(item => console.log(`${item.version} ${item.fileName} ${item.url}`));
    this.indexerService.indexFiles(indexableItems, this.selectedFiles);
  }

  private getIndexableItem(row: string): IndexableItem {
    const items = row.split(',');
    const indexItem: IndexableItem = {version: `${items[0]}`, fileName: `${items[1]}`, url: `${items[2]}`};
    return indexItem;
  }

}
